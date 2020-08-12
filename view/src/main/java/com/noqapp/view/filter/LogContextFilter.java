package com.noqapp.view.filter;

import com.noqapp.search.elastic.config.IPGeoConfiguration;

import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * User: hitender
 * Date: 11/19/16 4:42 PM
 */
@SuppressWarnings({
    "PMD.BeanMembersShouldSerialize",
    "PMD.LocalVariableCouldBeFinal",
    "PMD.MethodArgumentCouldBeFinal",
    "PMD.LongVariable"
})
public class LogContextFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(LogContextFilter.class);

    private static final Pattern EXTRACT_ENDPOINT_PATTERN = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
    private static final String REQUEST_ID_MDC_KEY = "X-REQUEST-ID";
    private IPGeoConfiguration ipGeoConfiguration;

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        String uuid = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_MDC_KEY, uuid);

        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        Map<String, String> headerMap = getHeadersInfo(httpServletRequest);
        String url = httpServletRequest.getRequestURL().toString();
        String query = httpServletRequest.getQueryString();
        String ip = getHeader(headerMap, "x-forwarded-for");
        String countryCode = "";
        String city = "";
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = ipGeoConfiguration.getDatabaseReader().city(ipAddress);
            countryCode = response.getCountry().getIsoCode();
            city = StringUtils.isEmpty(response.getCity().getName()) ? "" : response.getCity().getName();
        } catch (AddressNotFoundException e) {
            LOG.warn("Failed finding ip={} reason={}", ip, e.getLocalizedMessage());
        } catch (GeoIp2Exception e) {
            LOG.error("Failed reason={}", e.getLocalizedMessage(), e);
        }

        LOG.info("Request received:"
            + " host=\"" + getHeader(headerMap, "host") + "\""
            + " userAgent=\"" + getHeader(headerMap, "user-agent") + "\""
            + " accept=\"" + getHeader(headerMap, "accept") + "\""
            + " ip=\"" + ip + "\""
            + " country=\"" + countryCode + "\""
            + " city=\"" + city + "\""
            + " endpoint=\"" + extractDataFromURL(url, "$5") + "\""
            + " query=\"" + (query == null ? "none" : query) + "\""
            + " url=\"" + url + "\""
        );

        if (isHttpHead(httpServletRequest)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) res;
            NoBodyResponseWrapper noBodyResponseWrapper = new NoBodyResponseWrapper(httpServletResponse);

            chain.doFilter(new ForceGetRequestWrapper(httpServletRequest), noBodyResponseWrapper);
            noBodyResponseWrapper.setContentLength();
        } else {
            chain.doFilter(req, res);
        }
    }

    private String getHeader(Map<String, String> allHeadersMap, String header) {
        return CollectionUtils.isEmpty(allHeadersMap) && !allHeadersMap.containsKey(header) ? "" : allHeadersMap.get(header);
    }

    private String extractDataFromURL(String uri, String group) {
        return EXTRACT_ENDPOINT_PATTERN.matcher(uri).replaceFirst(group);
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    public void init(FilterConfig filterConfig) {
        LOG.info("Initialized logContextFilter");

        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        this.ipGeoConfiguration = ctx.getBean(IPGeoConfiguration.class);
    }

    public void destroy() {
        LOG.info("Destroyed logContextFilter");
    }

    /**
     * Deals with HTTP HEAD requests and response for all controllers. Even if these controllers are secured its better
     * to treat them nicely and not fail on HEAD request.
     * <p>
     * Added support for HEAD method in filter to prevent failing on HEAD request. As of now there is no valid
     * reason why filter contains this HEAD request as everything is secure after login and there are no bots or
     * crawlers when a valid user has logged in. We plan to use this until a decision would be made in near future.
     * <p>
     * The reason for this addition has already been fixed in code at location below.
     *
     * @see com.noqapp.view.controller.open.IndexController#index(org.springframework.ui.ModelMap)
     */
    private boolean isHttpHead(HttpServletRequest request) {
        return "HEAD".equals(request.getMethod());
    }

    private static class ForceGetRequestWrapper extends HttpServletRequestWrapper {
        public ForceGetRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public String getMethod() {
            return "GET";
        }
    }

    private class NoBodyResponseWrapper extends HttpServletResponseWrapper {
        private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream();
        private PrintWriter writer;

        public NoBodyResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public ServletOutputStream getOutputStream() {
            return noBodyOutputStream;
        }

        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(noBodyOutputStream, getCharacterEncoding()));
            }

            return writer;
        }

        void setContentLength() {
            super.setContentLength(noBodyOutputStream.getContentLength());
        }
    }

    private class NoBodyOutputStream extends ServletOutputStream {
        private int contentLength = 0;

        int getContentLength() {
            return contentLength;
        }

        public void write(int b) {
            contentLength++;
        }

        public void write(byte buf[], int offset, int len) throws IOException {
            contentLength += len;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}

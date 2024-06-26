<#assign ftlDateTime = .now>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta charset="utf-8">
    <title>NoQueue</title>
    <meta content='IE=edge,chrome=1' http-equiv='X-UA-Compatible'/>
    <meta content='width=device-width, initial-scale=1' name='viewport'/>
    <meta name="description" content="Complete your booking on NoQueue. Get instant token and real-time status. Search for doctors, hospital related service, doctors timing, pay online.">

    <link rel="stylesheet" href="${page.parentHost}/static/internal/css/style.css" type='text/css'/>
    <link rel="stylesheet" href="${page.parentHost}/static/internal/css/phone-style.css" type='text/css' media="screen"/>

    <style type="text/css">
        p {
            padding: 0 0 0 0; !important;
        }
    </style>

    <!-- Global site tag (gtag.js) - Google Analytics -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-101872684-1"></script>
    <script>
        window.dataLayer = window.dataLayer || [];
        function gtag(){dataLayer.push(arguments);}
        gtag('js', new Date());

        gtag('config', 'UA-101872684-1');
    </script>
</head>

<body>


<div class="main-warp">
    <!-- header -->
    <div class="header">
        <div class="warp-inner">
            <div class="logo">
                <a href="${page.parentHost}"><img src="${page.parentHost}/static/internal/img/logo.png" alt="NoQueue"/></a>
            </div>
        </div>
    </div>
    <!-- header end -->

    <!-- content -->
    <div class="content">
        <div class="warp-inner">
            <!-- login-box -->
            <div class="qr-box">
                <div class="qr-data">
                    <div class="qr-address">
                        <img src="${profile['profileImage']}" alt="Profile Image" class="img-profile-circle"/>
                        <h3>Dr. ${profile['name']}</h3>
                        <#if profile["education"]?has_content>
                            <p>${profile['education']}</p>
                        </#if>
                        <p>${profile['categoryName']}</p>
                        <#--<p>${profile['gender']}</p>-->
                        <#if profile['experienceDuration'] != 0>
                            <p>${profile['experienceDuration']}+ years of experience</p>
                        </#if>
                    </div>
                    <br/>

                    <#if profile["awards"]?has_content>
                        <h4>Awards</h4>
                        <div class="qr-address">
                            <#list profile["awards"] as nameDatePair>
                                <p>${nameDatePair.monthYear?date("yyyy-MM-dd")?string["MMM, yyyy"]} - ${nameDatePair.name}</p>
                            </#list>
                        </div>
                    </#if>

                    <h2>Available at Clinics:</h2>
                    <#list stores as key, value>
                        <div style="border:1px solid #ccc; margin: 10px 0px 20px 0px; padding: 10px;">
                            <div class="qr-address">
                                <h3>${value.bizName}</h3>
                                <p>${value.storeAddress}</p>
                                <#--<p>&nbsp;</p>-->
                                <#--<p>${value.phone}</p>-->
                            </div>
                            <div class="qr-queue" style="padding-bottom: 10px;">
                                <#--<h3>${value.displayName}</h3>-->
                                <p>${value.rating} &nbsp;
                                    <span id="store_rating_${key}"></span>&nbsp;&nbsp;${value.reviewCount} Reviews &nbsp;
                                </p>
                                <#if value.storeClosed == "Yes">
                                    <p><strong>Closed Today</strong></p>
                                <#else>
                                    <p><strong>${value.dayOfWeek} Hours: </strong> ${value.startHour} - ${value.endHour}</p>
                                </#if>

                                </br>
                                <p><strong>Queue Status: </strong>${value.queueStatus}</p>
                                <p><strong>Currently Serving: </strong>${value.currentlyServing}</p>
                                <p><strong>People in Queue: </strong>${value.peopleInQueue}</p>

                                <div class="button-btn" style="margin-bottom: 50px;">
                                    <form action="${page.https}://${page.domain}/open/join/queue/${value.codeQR}">
                                        <#if value.claimed == "No">
                                            <p style="padding: 20px 20px 20px 0; color: #9f1313">Not accepting Walk-ins</p>
                                        <#elseif value.storeClosed == "Yes">
                                            <button class="ladda-button next-btn" style="width:48%; float: left; background: grey; border: grey;">Closed Queue</button>
                                        <#else>
                                            <button class="ladda-button next-btn" style="width:48%; float: left;">Join Queue</button>
                                        </#if>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </#list>

                    <div class="download-app-icon">
                        <p>Get NoQueue</p>
                        <div>
                            <#--<a href="https://itunes.apple.com/us/app/noqapp/id1237327532?ls=1&mt=8">-->
                            <#--<img src="${page.parentHost}/static/internal/img/apple-store.png"/>-->
                            <#--</a>-->
                            <a href="https://play.google.com/store/apps/details?id=com.noqapp.android.client">
                                <img src="${page.parentHost}/static/internal/img/google-play.png"/>
                            </a>
                        </div>
                    </div>

                    <div class="qr-footer">
                        <p>TM and Copyright &copy; 2021 NoQueue</p>
                        <p>All Rights Reserved &nbsp; | &nbsp; <a href="${page.parentHost}/#/pages/privacy">Privacy Policy</a>
                            &nbsp; | &nbsp; <a href="${page.parentHost}/#/pages/terms">Terms</a></p>
                    </div>
                </div>
            </div>

            <!-- login-box -->

        </div>
    </div>
    <!-- content end -->


    <!-- Footer -->

    <!-- Footer End -->

</div>


</body>
<script type="text/javascript" src="${page.https}://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript" src="${page.parentHost}/static/external/raty/jquery.raty.js"></script>
<script type="text/javascript">
    <#list stores as key, value>
    $('#store_rating_${key}').raty({
        score: ${value.rating},
        halfShow: true,
        readOnly: true,
        noRatedMsg: 'Not rated yet!',
        starHalf: '${page.parentHost}/static/external/raty/img/star-half.png',
        starOff: '${page.parentHost}/static/external/raty/img/star-off.png',
        starOn: '${page.parentHost}/static/external/raty/img/star-on.png',
        hints: ['Bad', 'Poor', 'Good', 'Best', 'Awesome']
    });
    </#list>
</script>
<script type="text/javascript" src="${page.https}://${page.domain}/static/external/ladda/js/spin.min.js"></script>
<script type="text/javascript" src="${page.https}://${page.domain}/static/external/ladda/js/ladda.min.js"></script>
<script type="text/javascript">
    // Bind normal buttons
    Ladda.bind('.button-btn button', {timeout: 6000});

    // Bind progress buttons and simulate loading progress
    Ladda.bind('.progress-demo button', {
        callback: function (instance) {
            var progress = 0;
            var interval = setInterval(function () {
                progress = Math.min(progress + Math.random() * 0.1, 1);
                instance.setProgress(progress);

                if (progress === 1) {
                    instance.stop();
                    clearInterval(interval);
                }
            }, 200);
        }
    });

    // You can control loading explicitly using the JavaScript API
    // as outlined below:

    // var l = Ladda.create( document.querySelector( 'button' ) );
    // l.start();
    // l.stop();
    // l.toggle();
    // l.isLoading();
    // l.setProgress( 0-1 );
</script>
</html>


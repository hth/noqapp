<#assign ftlDateTime = .now>
<html>
<style type="text/css">
	@import url('http://fonts.googleapis.com/css?family=Open+Sans');

	body {
		margin: 0 20px 100px 20px;
		mso-line-height-rule: exactly;
		padding: 10px 30px 30px 30px;
		min-width: 90%;
		font-size: 13px;
		font-family: "Open Sans", sans-serif;
		letter-spacing: 0.02em;
		color: black;
	}

	.tm {
		letter-spacing: 0.05em;
		font-size: 8px !important;
		color: #4b5157;
		vertical-align: super;
	}

	@media only screen and (min-width: 368px) {
		.tm {
			font-size: 10px !important;
		}
	}
</style>
<body>
<#include "../NoQApp.svg">
<p style="padding-top: 10px;">
	Hey,
</p>
<p>
	Someone requested an account recovery on NoQueue's NoQApp for ${contact_email}, but we don’t have an account
	on this site that matches this email address.
</p>
<p>
	If you would like to create an account on NoQueue just visit our sign-up page:
	<a href="${https}://${domain}/open/registrationMerchant.htm">${https}://${domain}/open/registrationMerchant.htm ></a>
</p>
<p>
	If you did not request this account recovery, just ignore this email. Your email address is safe.
</p>
<p>
	Thanks,
	<br/>
	NoQueue Customer Support
</p>
<br/><br/><br/>
<hr/>
<span class="tm">
    TM &trade; and Copyright &copy; 2017 NoQueue Inc. Sunnyvale, CA 94085 USA. <br/>
    All Rights Reserved / <a href="https://www.noqapp.com/privacypolicy">Privacy Policy</a>
</span>
<br/>
<span class="tm">
    S:${ftlDateTime?iso("PST")}
</span>
</body>
</html>
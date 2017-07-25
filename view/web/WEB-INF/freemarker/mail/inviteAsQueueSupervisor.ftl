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
<p>
	Hey,
</p>
<p>
	You have been invited to manage queue ${displayName}. Please log in your account and complete the invitation process.
	This invite will expire after 7 days from now.
</p>
<p>
	Cheers, <br/>
	${businessName}<br />
</p>
<br/>
<p>
	NoQueue Customer Support would like to hear from you if you would not like to receive emails from us.
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
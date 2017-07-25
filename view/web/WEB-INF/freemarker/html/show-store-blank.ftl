<#assign ftlDateTime = .now>
<html>
<style type="text/css">
	@import url('http://fonts.googleapis.com/css?family=Open+Sans');
	@import url('https://receiptofi.com/css/style.css');

	body {
		margin: 0;
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
	No store found.
</p>
<br/><br/><br/>
<p>
	Download NoQApp to ...
</p>
<p>
	<a href="https://itunes.apple.com/us/app/receiptapp/id1044054989?ls=1&mt=8"><img class="app_store"></a>
	&nbsp;
	<a itemprop="downloadUrl" href="https://play.google.com/store/apps/details?id=com.receiptofi.receiptapp"><img class="google_play"></a>
</p>
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
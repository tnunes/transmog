<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>BlueWebTemplates</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="<c:url value="/styles/style.css" />"
	type="text/css" media="screen, projection">
<link rel="stylesheet" href="<c:url value="/styles/style.css" />"
	type="text/css" media="print"><!--[if lt IE 8]>
		<link rel="stylesheet" href="<c:url value="/styles/style.css" />" type="text/css" media="screen, projection">
	<![endif]-->
<script type="text/javascript" src="<c:url value="/scripts/jquery-1.4.2.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/scripts/jquery.cycle.all.min.js" />"></script>
<script type="text/javascript">
$(document).ready(function(){
    $('#slideshow').cycle({
        fx:     'fade',
        speed:  'slow',
        timeout: 5000,
        pager:  '#slider_nav',
        pagerAnchorBuilder: function(idx, slide) {
            // return sel string for existing anchor
            return '#slider_nav li:eq(' + (idx) + ') a';
        }
    });
});
</script>
</head>
<body>
<div class="main">
  <div class="header_resize">
    <div class="header">
      <div class="logo"><a href="index.html"><img src="images/logo.gif" width="143" height="97" border="0" alt="logo" /></a></div>
      <div class="menu">
        <ul>
          <li><a href="index.html" class="active">Home</a></li>
          <li><a href="about.html">About Us</a></li>
          <li><a href="work.html">Gallery </a></li>
          <li><a href="work.html">Blog</a></li>
          <li><a href="contact.html">Contact Us</a></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
  </div>
  <div class="header_blog">
    <div class="resize">
      <div class="search">
        <form id="form1" name="form1" method="post" action="">
          <span>
          <input name="q" type="text" class="keywords" id="textfield" maxlength="50" value="Search..." />
          </span>
          <input name="b" type="image" src="images/search.gif" class="button" />
        </form>
      </div>
      <h2>Your company's welcome message or slogan. Lorem ipsum dolor sit amet, consectur el.</h2>
      <p>Donec metus lacus, porta id, auctor sit amet, aliquam eu, lacus. Quisque sagittis vulputate orci.</p>
    </div>
    <div id="slider">
      <!-- start slideshow -->
      <div id="slideshow">
        <div class="slider-item"><a href="#"><img src="images/simple_img_1.jpg" alt="icon" width="963" height="404" border="0" /></a></div>
        <div class="slider-item"><a href="#"><img src="images/simple_img_2.jpg" alt="icon" width="963" height="404" border="0" /></a></div>
        <div class="slider-item"><a href="#"><img src="images/simple_img_3.jpg" alt="icon" width="963" height="404" border="0" /></a></div>
      </div>
      <!-- end #slideshow -->
      <div class="controls-center">
        <div id="slider_controls">
          <ul id="slider_nav">
            <li><a href="#"></a></li>
            <!-- Slide 1 -->
            <li><a href="#"></a></li>
            <!-- Slide 2 -->
            <li><a href="#"></a></li>
            <!-- Slide 3 -->
          </ul>
        </div>
      </div>
    </div>
    <div class="clr"></div>
  </div>
  <div class="body">
    <div class="body_resize">
      <div class="body_resize_top">
        <div class="body_resize_bottom">
          <div class="left">
            <h2>About Us</h2>
            <img src="images/img_1.jpg" alt="img" width="234" height="102" />
            <p>Morbi pharetra mollis tempus. Donec leo urna, sodales vel imperdiet ut, adipiscing ac mi. Praesent laoreet imperdiet orci in posuere. </p>
          </div>
          <div class="right">
            <h2>Fresh Work</h2>
            <div class="left_blog"> <img src="images/img_2.jpg" alt="img" width="196" height="102" />
              <p><span>Portfolio Item</span><br />
                Quisque ipsum lorem, interdum et adipiscing vitae, venenatis ac lectus. </p>
            </div>
            <div class="left_blog"> <img src="images/img_3.jpg" alt="img" width="196" height="102" />
              <p><span>Portfolio Item</span><br />
                Quisque ipsum lorem, interdum et adipiscing vitae, venenatis ac lectus. </p>
            </div>
            <div class="left_blog"> <img src="images/img_4.jpg" alt="img" width="196" height="102" />
              <p><span>Portfolio Item</span><br />
                Quisque ipsum lorem, interdum et adipiscing vitae, venenatis ac lectus. </p>
            </div>
          </div>
          <div class="clr"></div>
        </div>
      </div>
    </div>
    <div class="clr"></div>
  </div>  
  <div class="FBG">
    <div class="FBG_resize">
      <div class="left">
        <h2>About </h2>
        <ul>
          <li><a href="#">Overview</a></li>
          <li> <a href="#">Another Link</a></li>
          <li> <a href="#">Our Company</a></li>
          <li> <a href="#">Our Staff</a></li>
          <li> <a href="#">Mission </a></li>
          <li> <a href="#">Statement</a></li>
        </ul>
      </div>
      <div class="left">
        <h2>Services</h2>
        <ul>
          <li><a href="#">First Service</a></li>
          <li> <a href="#">Second Service</a></li>
          <li> <a href="#">Another Service</a></li>
          <li> <a href="#">A Fourth Service</a></li>
        </ul>
      </div>
      <div class="left">
        <h2>Media</h2>
        <ul>
          <li><a href="#">Image Gallery</a></li>
          <li> <a href="#">Video Gallery</a></li>
          <li> <a href="#">Audio Files</a></li>
          <li> <a href="#">The Podcast</a></li>
        </ul>
      </div>
      <div class="left">
        <h2>Blog</h2>
        <ul>
          <li><a href="#">Category One</a></li>
          <li> <a href="#">Category 2</a></li>
          <li> <a href="#">Another Category</a></li>
          <li> <a href="#">Category</a></li>
          <li> <a href="#">A Fifth Category</a></li>
        </ul>
      </div>
      <div class="left">
        <h2>More Links</h2>
        <ul>
          <li><a href="#">Sign Up Now</a></li>
          <li> <a href="#">We Are Hiring</a></li>
          <li> <a href="#">Terms &amp; Conditions</a></li>
          <li> <a href="#">Privacy Policy</a></li>
        </ul>
      </div>
      <div class="left">
        <h2>Contact Us</h2>
        <ul>
          <li><a href="#">Email Sales</a></li>
          <li> <a href="#">Email Support</a></li>
          <li> <a href="#">Phone: 555-555-3211</a></li>
          <li> <a href="#">Send Us Feedback</a></li>
        </ul>
      </div>
      <div class="clr"></div>
    </div>
    <div class="clr"></div>
  </div>
  <div class="footer">
    <div class="footer_resize"> <a href="#"><img src="images/footer_logo.gif" alt="picture" width="270" height="60" border="0" /></a>
      <p class="rightt">© Copyright Your Site Name Dot Com. All Rights Reserved <br />
        <a href="#">Home</a> | <a href="#">Contact</a> | <a href="#">RSS </a> (Blue) <a href="http://www.bluewebtemplates.com">Website Templates</a></p>
      <div class="clr"></div>
    </div>
  </div>
</div>
</body>
</html>
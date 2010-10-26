<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta http-equiv="content-language" content="en" />
    <meta name="robots" content="all,follow" />
    <meta name="author" lang="en" content="All: ... [www.url.com]; e-mail: info@url.com" />
    <meta name="copyright" lang="en" content="Webdesign: Nuvio [www.nuvio.cz]; e-mail: ahoj@nuvio.cz" />
    <meta name="description" content="..." />
    <meta name="keywords" content="..." />
    <link rel="stylesheet" media="screen,projection" type="text/css" href="<c:url value="/styles/reset.css" />" />
    <link rel="stylesheet" media="screen,projection" type="text/css" href="<c:url value="/styles/main.css" />" />
    <!--[if lte IE 6]><link rel="stylesheet" media="screen,projection" type="text/css" href="<c:url value="/styles/main-ie6.css" />" /><![endif]-->
    <link rel="stylesheet" media="screen,projection" type="text/css" href="<c:url value="/styles/style.css" />" />
	<script type="text/javascript" src="<c:url value="/scripts/jquery-1.4.2.min.js" />"></script>
	<script type="text/javascript" src="<c:url value="/scripts/jquery.innerfade.js" />"></script>
    <script type="text/javascript">
    $(document).ready(
    function(){
        $('#slider').innerfade({
            animationtype: 'fade',
            speed: 750,
            timeout: 3000,
            type: 'sequence',
            containerheight: 'auto'
        });
    });
    </script>
    <title>Simplify</title>
</head>

<body>

<div id="main">

    <!-- Header -->
    <div id="header" class="box">

        <p id="logo"><a href="./" title="Your Company [Go to homepage]"><img src="design/logo.gif" alt="Logo" /></a></p>

        <hr class="noscreen" />

        <!-- Navigation -->
        <p id="nav">
            <strong>Home</strong> <span>|</span>
            <a href="#">About</a> <span>|</span>
            <a href="#">Portfolio</a> <span>|</span>
            <a href="#">Tutorials</a> <span>|</span>
            <a href="#">Contact</a>
        </p>

    </div> <!-- /header -->

    <hr class="noscreen" />

    <!-- Promo -->
    <div id="promo">

        <p id="slogan"><img src="design/slogan.gif" alt="Place for your slogan" /></p>
        
        <ul id="slider">
            <li><img src="<c:url value="/images/slider/slider-01.jpg" />" alt="" /></li>
            <li><img src="<c:url value="/images/slider/slider-01.jpg" />" alt="" /></li>
            <li><img src="<c:url value="/images/slider/slider-01.jpg" />" alt="" /></li>
        </ul>
        
    </div> <!-- /promo -->

    <hr class="noscreen" />

    <!-- Three columns -->
    <div class="cols3">
    
        <div class="cols3-content box">

            <!-- Column -->
            <div class="col">
            
                <h2><a href="#">Lorem ipsum</a></h2>
                
                <p><a href="#"><img src="images/col-01.jpg" class="block" alt="" /></a></p>

                <p class="smaller"><strong>Lorem ipsum dolor sit amet, consectetur eseli.</strong>
                Vestibulum at dolor vel risus scelerisque lobortis vitae
                hendrerit dui. Culi sociis natoque penatibus et magnis.</p>

                <ul>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                </ul>
            
            </div> <!-- /col -->
            
            <!-- Column -->
            <div class="col">

                <h2><a href="#">Lorem ipsum</a></h2>
                
                <p><a href="#"><img src="images/col-02.jpg" class="block" alt="" /></a></p>

                <p class="smaller"><strong>Lorem ipsum dolor sit amet, consectetur eseli.</strong>
                Vestibulum at dolor vel risus scelerisque lobortis vitae
                hendrerit dui. Culi sociis natoque penatibus et magnis.</p>

                <ul>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                </ul>
                
            </div> <!-- /col -->
            
            <!-- Column -->
            <div class="col last">

                <h2><a href="#">Lorem ipsum</a></h2>
                
                <p><a href="#"><img src="images/col-03.jpg" class="block" alt="" /></a></p>

                <p class="smaller"><strong>Lorem ipsum dolor sit amet, consectetur eseli.</strong>
                Vestibulum at dolor vel risus scelerisque lobortis vitae
                hendrerit dui. Culi sociis natoque penatibus et magnis.</p>

                <ul>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                    <li><a href="#">Lorem ipsum dolor sit amet</a></li>
                </ul>

            </div> <!-- /col -->

        </div> <!-- /cols3-content -->
        
        <div class="cols3-bottom"></div>

    </div> <!-- /cols3 -->

    <!-- Footer -->
    <div id="footer" class="box">

        <!-- DONÂ´T REMOVE THIS LINE -->
        <p class="f-right"><a href="http://www.nuviotemplates.com/">Free web templates</a> by <a href="http://www.qartin.cz/">Qartin</a>, sponsored by: <a href="http://www.grily-krby.cz/">Grily</a></p>

        <p class="f-left">&copy;&nbsp;2009 <a href="#">Your Company Inc.</a></p>

    </div> <!-- /footer -->

</div> <!-- /main -->

</body>
</html>
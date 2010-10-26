<?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
    <title><spring:message code="title.search"/></title>
    <script type="text/javascript">
	    function submitSearch(myfield, e){
	    	var keycode;
	    	if (window.event)
	    		keycode = window.event.keyCode;
	    	else if (e)
	    		keycode = e.which;
	    	else
	    		return true;
	    	if (keycode == 13) {
	    		var searchTxt = $.trim(myfield.value);
		    	if(searchTxt){
		    		var location = "<c:url value="/query/"/>";
		    		if(location.indexOf("?") > 0){
		    			location = location.substring(0, location.indexOf("?"));
		    		}
		    		window.location.href = location+searchTxt+".html";
		    		return false;
	    		}else{
	    			alert("<spring:message code="warning.blankSearchString"/>");
	    			return false;
	    		}
	    	} else
	    		return true;
	    }
    </script>
</head>

<body>
<div id="main">

    <!-- Header -->
    <div id="header" class="box">

        <p id="logo"><a href="./" title="Concept Store Manager [Go to homepage]"><img src="<c:url value="/images/logo.gif" />" alt="Logo" /></a></p>
        <hr class="noscreen" />

        <!-- Navigation -->
        <p id="nav">
            <strong><spring:message code="label.home"/></strong> <span>|</span>
            <a href="#"><spring:message code="label.about"/></a> <span>|</span>
            <a href="#"><spring:message code="label.papers"/></a> <span>|</span>
            <a href="#"><spring:message code="label.tutorials"/></a> <span>|</span>
            <a href="#"><spring:message code="label.contact"/></a> <span>|</span>
           	<input id="searchTxt" name="searchTxt" type="text" size="20" class="input" onkeypress="return submitSearch(this,event)"/>
        </p>

    </div> <!-- /header -->

    <hr class="noscreen" />

    <h1 id="title"><spring:message code="label.searchResultsFor"/> "<c:out value="${queryResultResponse.searchText}"/>"</h1>

    <!-- Sub-navigation -->
    <p id="subnav">
        <strong><spring:message code="label.previousSearch"/>
        <c:forEach var="previousSearchText" items="${queryResultResponse.previousSearchTexts}" begin="0" end="5">
        	</strong> <span>|</span>
        	<a href="<c:url value="/query/${previousSearchText}" />">
        		<c:out value="${previousSearchText}" />
        	</a>
        </c:forEach>
    </p>

    <!-- Three columns -->
    <div class="content box">
        <div class="content-in box">
            <!-- Perex -->
            <div class="perex">
                <p>
                	Google word cloud visualization will be shown here. Coming soon. [Response ${queryResultResponse.responseType}].
                </p>
            </div> <!-- /perex -->
            
			<!--Query Results-->
			 
			<c:choose> 
	        	<c:when test="${queryResultResponse.responseType == SUCCESS}">
	        		<h2><spring:message code="label.searchResultsByRelevance"/></h2>
	        	</c:when>
	        	<c:otherwise>
	        		<h2><c:out value="${queryResultResponse.message}"/></h2>
	        	</c:otherwise>
	        </c:choose>
            <c:if test="${queryResultResponse.responseType == 'SUCCESS'}">
            <table width="100%">
                <tr>
                    <th width="80%"><spring:message code="label.label"/></th>
                    <th><spring:message code="label.language"/></th>
                </tr>
                <c:forEach varStatus="status" var="queryResult" items="${queryResultResponse.queryResults}">
                		<c:choose> 
	                		<c:when test="${status.index % 2 == 0}">
	                			<c:set var="trClass" value="even"/>
	                		</c:when>
	                		<c:otherwise>
	                			<c:set var="trClass" value="odd"/>
	                		</c:otherwise>
	                	</c:choose>
                		<tr class="${trClass}">
                			<td><a href="<c:url value="/concept/${queryResult.id}" />"><c:out value="${queryResult.label}"/></a></td>
                			<td><spring:message code="language.${queryResult.language}" text="Other"/></td>
                		</tr>
                </c:forEach>
            </table>
            
            <p class="nomt"><strong><spring:message code="label.responseSummary" arguments="${queryResultResponse.responseTime/1000}"/></strong><br /></p>
            <div class="fix"></div>
		</c:if>
        </div> <!-- /content-in -->
        <div class="content-bottom"></div>
    </div> <!-- /content -->
    <!-- Footer -->
    <div id="footer" class="box">

        <!-- DONÂ´T REMOVE THIS LINE -->
        <p class="f-right"><a href="http://www.nuviotemplates.com/">Free web templates</a> by <a href="http://www.qartin.cz/">Qartin</a>, sponsored by: <a href="http://www.grily-krby.cz/">Grily</a></p>

        <p class="f-left">&copy;&nbsp;2010 <a href="http://biosemantics.org">biosemantics</a>, Deptt. of Medical Informatics, Erasmus Medical Centre</p>

    </div> <!-- /footer -->

</div> <!-- /main -->

</body>
</html>
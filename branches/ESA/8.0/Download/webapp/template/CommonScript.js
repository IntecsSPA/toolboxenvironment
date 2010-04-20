/* This file contains a snumber of javascript which are available inside all mass pages.
   They are included from the mass template.
   They can be sued inside stylesheet to avoid the problem with characters such as <, >, &....
*/

/* add breadcrumb at top of page */
function addBreadCrumb(html)
{
	var path = document.getElementById("breadCrumb");
	path.innerHTML = html;
}


function addHelp(key)
{
	//add the link to the infocenter
	var path = document.getElementById("infoCenter");
	path.innerHTML = "<a href='help.jsp?topic=" + key + "' target='_blank'>Help</a>";	
}



/* add infocenter link at top of page
   @param path the jsp path
   @param linkMessage the message for the link
 */
function addInfoCenter(servletPath, contextPath, linkMessage)
{
	//create the mapping tab between the infocenter pages and the portal pages
	var mapping = new Array();
	
	//mapping for the home page 
	mapping["MyHomePage.jsp"]="134.htm";
	
	//mapping for the user registration page
	mapping["UserRegistrationUI.jsp"]="131.htm";
	
	//mapping for the user agreement page
	mapping["UserAgreement.jsp"]="159.htm";
	
	//mapping for user registration confirmation page
	mapping["UserRegistrationConfirmUI.jsp"]="161.htm";
	
	//mapping for user login page
	mapping["UserLoginFormUI.jsp"]="133.htm";
	
	//mapping for user update page
	mapping["UserUpdateUI.jsp"]="130.htm";
	
	//mapping for the order info page (for an anonymous user)
	mapping["OrderIdUI.jsp"]="104.htm";
	
	//mapping for order list page (for a registered user)
	mapping["OrderListUI.jsp"]="106.htm";
	
	//mapping for the order info page (informations about the order)
	mapping["OrderInfoUI.jsp"]="102.htm";
	
	//mapping for service category directory page
	mapping["ServiceCategoryDirectoryUI.jsp"]="118.htm";
	
	//mapping for company category directory page
	mapping["CompanyCategoryDirectoryUI.jsp"]="117.htm";
	
	//mapping for the search page (search in a catalogue)
	mapping["SearchProcessUI.jsp"]="138.htm";
	
	//mapping for the service registration page
	mapping["ServiceRegistrationUI.jsp"]="120.htm";
	
	//mapping for the AOI configuration page
	mapping["DisplayAOIConfigUI.jsp"]="120.htm";
	
	//mapping for the advance search page
	mapping["AdvancedSearchUI.jsp"]="111.htm";
	
	//mapping for the about us page
	mapping["AboutUsUI.jsp"]="121.htm";
	
	//mapping for the company directory page
	mapping["CompanyDirectoryUI.jsp"]="115.htm";
	
	//mapping for company info page
	mapping["CompanyInfoUI.jsp"]="113.htm";
	
	//mapping for the service directory page
	mapping["ServiceDirectoryUI.jsp"]="116.htm";
	
	//mapping for service detailed info page
	mapping["ServiceDetailedInfoUI.jsp"]="114.htm";

	//mapping for the order preparation page (order a service)
	mapping["OrderPreparationUI.jsp"]="109.htm";
	
	//mapping for the service order confirmation page
	mapping["OrderConfirmationUI.jsp"]="108.htm";

	//mapping for the order report page
	mapping["OrderReportUI.jsp"]="146.htm";
	
	//mapping for the shopping basket page
	mapping["ShoppingBasketUI.jsp"]="147.htm";
	
	//mapping for service rating page
	mapping["ServiceRatingUI.jsp"]="112.htm";
	
	//mapping for transforming error page
	mapping["TransformingErrorPageUI.jsp"]="2.htm";

	//mapping for contact us page
	mapping["FeedbackUI.jsp"]="124.htm";
	
	//mapping for documents page
	mapping["HelpUI.jsp"]="123.htm";
	
	//mapping for news item list page
	mapping["NewsItemListUI.jsp"]="126.htm";
	
	//mapping for news item content page
	mapping["NewsItemContentUI.jsp"]="125.htm";
	
	//mapping for service provider agreement page
	mapping["ServiceProviderAgreement.jsp"]="158.htm";
	
	//mapping for apply to be service provider confirmation page
	mapping["ApplyToBeServiceProviderUI.jsp"]="157.htm";
	
	//mapping for user feedback page
	mapping["UserFeedbackUI.jsp"]="160.htm";
	
	//mapping for company registration page
	mapping["CompanyRegistrationUI.jsp"]="128.htm";
	
	//mapping for service provider update page
	mapping["ServiceProviderUpdateUI.jsp"]="129.htm";
	
	//mapping for forgot password result page
	mapping["ForgotPasswordResultUI.jsp"]="132.htm";
	
	//mapping for forgot password page
	mapping["ForgotPasswordUI.jsp"]="132.htm";
	
	//mapping for user unregistration page
	mapping["UserUnregistrationUI.jsp"]="127.htm";
	
	//mapping for user unregistration error page
	mapping["UnregisterErrorUI.jsp"]="162.htm";
	
	//mapping for download page
	mapping["DownloadUtil.jsp"]="122.htm";
	
	//mapping for the list of service granting for a register user
	mapping["ListServiceGrantingInfoForRUUI.jsp"]="149.htm";
	
	//mapping for the list of service granting for a service provider
	mapping["ListServiceGrantingInfoForSPUI.jsp"]="176.htm";
	
	//mapping for portal success page (when a service is well created or well updated)
	mapping["Portal_SuccessPageUI.jsp"]="150.htm";
	
	//mapping for disclaimer page
	mapping["Disclaimer.jsp"]="153.htm";
	
	//mapping for the portal error page
	mapping["ErrorPageUI.jsp"]="154.htm";

	//mapping for the privacy policy page
	mapping["PrivacyPolicy.jsp"]="155.htm";
	
	//mapping for the search page
	mapping["SearchUI.jsp"]="156.htm";
	
	//mapping for order registration suggestion page
	mapping["OrderRegistrationSuggestionUI.jsp"]="163.htm";
	
	//mapping for order checkout result page
	mapping["OrderCheckoutResultUI.jsp"]="107.htm";
	
	//mapping for the order detailed information page
	mapping["OrderDetailedInfoUI.jsp"]="105.htm";
	
	//mapping for the order result info page
	mapping["OrderResultInfoUI.jsp"]="103.htm";
	
	//mapping for dds basic request access confirmation
	mapping["RequestAccessUserWarningUI.jsp"]="143.htm";
	
	//mapping for service granting information page
	mapping["ServiceGrantingInfoOfServiceUI.jsp"]="176.htm";
	

	//
	
	//mapping for service update page
	mapping["ServiceUpdateUI.jsp"]="119.htm";
	
	//mapping for service quotation page
	mapping["ServiceQuotationUI.jsp"]="164.htm";
	
	//mapping for the RFQ confirmation page
	mapping["RFQConfirmationUI.jsp"]="165.htm";
	
	//mapping for the RFQ checkout result page
	mapping["RFQCheckoutResultUI.jsp"]="166.htm";

	
	//get the portal page from the path to know where to go in the infocenter
	//first we split the path into a table which contains all elements(without the / symbol) of the path
	var pathElements = new Array();
	pathElements = servletPath.split("/");
	
	//we take the last element in the table corresponding to the jsp name 
	var portalPage = pathElements[(pathElements.length)- 1];
	
	//take the infocenter page corresponding to the portal page
	var infoCenterPage;
	infoCenterPage = mapping[portalPage];
	
	//add the link to the infocenter
	var path = document.getElementById("infoCenter");
	if(infoCenterPage != undefined) {
		path.innerHTML = "<a href='"+ contextPath + "/infocenter/?topic=/be.spacebel.sse.doc.user/content/page"+ infoCenterPage + "' target='_blank'>" + linkMessage + "</a>";
	} else {
		path.innerHTML = "<a href='"+ contextPath + "/infocenter/' target='_blank'>" + linkMessage + "</a>";
	}
	
}


/* compare a number = input to a second number introduced as string */
function isShorter(input, compared) {
        var comparedNum = parseInt(compared);
        if(comparedNum > input) {
            return true;
        } else {
            return false;
        }
    }



/* Check if a number is less than another number */
function isLessThanNumber(number, comparedNumber) {
    // Compare two numbers
    if (number < comparedNumber) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number is less than or equal to another number */
function isLessThanOrEqualToNumber(number, comparedNumber) {
    // Compare two numbers
    if (number <= comparedNumber) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number is greater than another number */
function isGreaterThanNumber(number, comparedNumber) {
    // Compare two numbers
    if (number > comparedNumber) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number is greater than or equal to another number */
function isGreaterThanOrEqualToNumber(number, comparedNumber) {
    // Compare two numbers
    if (number >= comparedNumber) {
        return true;
    } else {
        return false;
    }
}

/* Get the current date in format of CCYY-MM-DD */
function getCurrentDate() {
    var currentDate = new Date();
    var date = currentDate.getDate();
    var month = currentDate.getMonth() + 1;

    if (date <= 9) {
        date = "0" + date;
    }

    if (month <= 9) {
        month = "0" + month;
    }

    var currentDateStr = currentDate.getFullYear() + "-" + month + "-" + date;
    return currentDateStr;
}

/* Get the current date and time in format of CCYY-MM-DDThh:mm:ss
 * 'T' is the date/time separator.
 */
function getCurrentDateTime() {
    var currentDate = new Date();
    var date = currentDate.getDate();
    var month = currentDate.getMonth() + 1;
    var hour = currentDate.getHours();
    var minute = currentDate.getMinutes();
    var second = currentDate.getSeconds();

    if (date <= 9) {
        date = "0" + date;
    }

    if (month <= 9) {
        month = "0" + month;
    }

    if (hour <= 9) {
        hour = "0" + hour;
    }

    if (minute <= 9) {
        minute = "0" + minute;
    }

    if (second <= 9) {
        second = "0" + second;
    }

    /* 'T' is the date/time separator */
    var currentDateTimeStr = currentDate.getFullYear() + "-" + month + "-" + date +
                         "T" + hour + ":" + minute + ":" + second;

    return currentDateTimeStr;
}

/* Check if a string is empty */
function checkEmptyString(str) {
    var index;

    if (str.length < 1) {
        return true;
    }

    for (index=0; index < str.length; index++) {
        if (str.charAt(index) != ' ') {
            return false;
        }
    }
    return true;
}

/* Check if a character belongs to a string */
function belongToStr(c, str) {
    for (var i = 0; i < str.length; i++) {
        if (c == str.charAt(i)) {
            return true;
        }
    }
    return false;
}

/* Check if a string is a numerical string */
function isNumericalString(str) {
    var numStr = "0123456789.,";

    // Check if the string is empty
    if (checkEmptyString(str)) {
        return false;
    }

    // Check if all the characters of the string are numerical character
    for (var i = 0; i < str.length; i++){
        var c = str.charAt(i);
        if (!belongToStr(c, numStr)) {
            return false;
        } else {
            if ( ( (i == 0) || (i == str.length - 1) ) &&
                 ( (c == '.') || (c == ',') )  ) {
                return false;
            }
        }
    }

    return true;
}

/* Check if a number contained in a numerical string is less than another integer number */
function isLessThanIntegerStr(intStr, comparedIntStr) {
    var intNum = parseInt(intStr);
    var comparedIntNum = parseInt(comparedIntStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(intStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (intNum < comparedIntNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is less than or equal to another integer number */
function isLessThanOrEqualToIntegerStr(intStr, comparedIntStr) {
    var intNum = parseInt(intStr);
    var comparedIntNum = parseInt(comparedIntStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(intStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (intNum <= comparedIntNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is greater than another integer number */
function isGreaterThanIntegerStr(intStr, comparedIntStr) {
    var intNum = parseInt(intStr);
    var comparedIntNum = parseInt(comparedIntStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(intStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (intNum > comparedIntNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is greater than or equal to another integer number */
function isGreaterThanOrEqualToIntegerStr(intStr, comparedIntStr) {
    var intNum = parseInt(intStr);
    var comparedIntNum = parseInt(comparedIntStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(intStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (intNum >= comparedIntNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is less than another float number */
function isLessThanFloatStr(floatStr, comparedFloatStr) {
    var floatNum = parseFloat(floatStr);
    var comparedFloatNum = parseFloat(comparedFloatStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(floatStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (floatNum < comparedFloatNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is less than or equal to another float number */
function isLessThanOrEqualToFloatStr(floatStr, comparedFloatStr) {
    var floatNum = parseFloat(floatStr);
    var comparedFloatNum = parseFloat(comparedFloatStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(floatStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (floatNum <= comparedFloatNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is greater than another float number */
function isGreaterThanFloatStr(floatStr, comparedFloatStr) {
    var floatNum = parseFloat(floatStr);
    var comparedFloatNum = parseFloat(comparedFloatStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(floatStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (floatNum > comparedFloatNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a number contained in a numerical string is greater than or equal to another float number */
function isGreaterThanOrEqualToFloatStr(floatStr, comparedFloatStr) {
    var floatNum = parseFloat(floatStr);
    var comparedFloatNum = parseFloat(comparedFloatStr);

    // Check if the original string is in numerical format
    if (!isNumericalString(floatStr)) {
        return false;
    }

    // Compare two numbers parsed from two strings
    if (floatNum >= comparedFloatNum) {
        return true;
    } else {
        return false;
    }
}

/* Check if a string is greater than another one */
function isGreaterThanOrEqualToString(str, comparedStr) {
    // Compare two strings
    if (str >= comparedStr) {
        return true;
    } else {
        return false;
    }
}
/* check if the number of selected options > to the comparedNumber
   field is assumed to be html option field in */
function checkNumberOfSelectedOptionsGT(field,comparedNumber)
{
  var count = 0;
  for (i=0; i < field.length; i++)
      if (field[i].selected ==true) count++;

  if (count > comparedNumber)
       return true;
  else
       return false;
}

/* Abstract function used to check all mandatory fields of a form */
function checkMandatoryFields(frm) {
    return true;
}
/** Check that a date is valid : isDate(strDay,strMonth,strYear)
 * strDay : string "01" "02"..
 * strMonth : string "01" "02"..
 * strYear : string "1901"...
 * DHTML date validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
 */
// Declaring valid date character, minimum year and maximum year

var minYear=1900;
var maxYear=2100;

function daysInFebruary (year){
	// February has 29 days in any year evenly divisible by four,
    // EXCEPT for centurial years which are not also divisible by 400.
    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
}
function DaysArray(n) {
	for (var i = 1; i <= n; i++) {
		this[i] = 31
		if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
		if (i==2) {this[i] = 29}
   }
   return this
}

function isDate(strDay,strMonth,strYear){


	var daysInMonth = DaysArray(12)

	strYr=strYear
	if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
	if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
	for (var i = 1; i <= 3; i++) {
		if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
	}
	month=parseInt(strMonth)
	day=parseInt(strDay)
	year=parseInt(strYr)

	if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
		return false
	}
	if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){

		return false
	}

    return true
}
/*
  set current date assumes that html selection objects are passed as input
  parameters : year with full values (2004, 2003...), month (01-12), day (01-31)
  it set the values of these parameters to the current date.
*/
function setCurrentDate(selectYear, selectMonth, selectDay)
{
  var currentDate = new Date();
  selectYear.value  = currentDate.getFullYear();
  var month = currentDate.getMonth() + 1;
  if (month <= 9) {
        month = "0" + month;
  }
  selectMonth.value = month;
  var day = currentDate.getDate();
  if (day <= 9) {
        day = "0" + day;
  }
  selectDay.value   = day;
}

/* Close the news of the day popup window */
function closePopup (itemId)
{
     var obj = document.getElementById(itemId);
     var div = document.getElementById('body');
     div.removeChild(obj);
	document.cookie = "nopopup" + "=" + 1
/*     setCookie("nopopup",1); */
}
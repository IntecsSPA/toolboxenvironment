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


var docUrl="Documentation/docsExplorer/index.html";
function addHelp(key)
{
    //add the link to the infocenter
    var path = document.getElementById("infoCenter");
    var html="<span class=\"jsAction\"  onclick=\"javascript:helpPopup('"+docUrl+"', '"+key+"')\" target=\"_blank\"><IMG title=\"help\" alt=\"help\" src=\"images/help.png\"></span>";
    path.innerHTML = html;
}

/*Open Help Full Screen Documentation Popup*/
var helpSectionPath=null;
var windowHelp=null;
function helpPopup(url, sectionPath)
{
    if (windowHelp == null)
    {
        helpSectionPath=sectionPath;
        var params  = 'width='+screen.width;
        params += ', height='+screen.height;
        params += ', top=0, left=0'
        params += ', fullscreen=yes';
        windowHelp=window.open(url,'TOOLBOX Help', params);
        windowHelp.onunload=function(){
            windowHelp=null;
        }
        if (window.focus) {
            windowHelp.focus()
        }
        return false;
    } else
    {
        helpSectionPath=sectionPath;
        windowHelp.location.reload();
        return false;
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
        if (i==4 || i==6 || i==9 || i==11) {
            this[i] = 30
        }
        if (i==2) {
            this[i] = 29
        }
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

/* Open Download Popup*/
function downloadPopup (url)
{
    window.open(url,'download','width=600, height=450, scrollbars=yes, menubar=yes');
}

/* Open Full Screen Popup*/
function fullScreenPopup (url,title)
{
    window.open(url,title,"width="+screen.width+", height="+screen.height+", scrollbars=yes, menubar=yes");
}





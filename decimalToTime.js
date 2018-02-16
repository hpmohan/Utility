//Input 4.5 ==>  4 hours 30 minutes 00 seconds 


var decimalTimeString = "4.30";
var decimalTime = parseFloat(decimalTimeString);
decimalTime = decimalTime * 60 * 60;
var hours = Math.floor((decimalTime / (60 * 60)));
decimalTime = decimalTime - (hours * 60 * 60);
var minutes = Math.floor((decimalTime / 60));
decimalTime = decimalTime - (minutes * 60);
var seconds = Math.round(decimalTime);
if(hours < 10)
{
	hours = "0" + hours;
}
if(minutes < 10)
{
	minutes = "0" + minutes;
}
if(seconds < 10)
{
	seconds = "0" + seconds;
}
console.log("" + hours + ":" + minutes + ":" + seconds);



//Input 4.5 ==>  4 hours 50 minutes 


var time = textValue1;
var hours = parseInt('0', 10);
var minutes = parseInt('00', 10);
if(time.toString().indexOf('.') !== -1){
    var tempArray = time.toString().split('.');
    hours = tempArray[0];
    minutes = tempArray[1];//Math.abs('.'+tempArray[1]);
    if(minutes >= 60 || minutes.toString().length > 2){
        component.find('repairLabourHours').set("v.value",hours);
        minutes = parseInt('0', 10);

    }
} else {
    hours = time;
}

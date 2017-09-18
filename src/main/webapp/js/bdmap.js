var map = new BMap.Map("l-map");
map.centerAndZoom(new BMap.Point(116.404, 39.915), 11);
map.enableScrollWheelZoom(true);
function myFun(result){
	var lon = document.getElementById("ad_longitude").value;
	var lat = document.getElementById("ad_latitude").value;
	if(lon==""||lat==""){
		var cityName = result.name;
		map.setCenter(cityName);
	}else{
		var ra = document.getElementById('radius').value;
		initMarker(lon,lat,ra);
	}
}
var myCity = new BMap.LocalCity();
myCity.get(myFun);

var ac = new BMap.Autocomplete(
	{"input" : "location",
	 "location" : map
	}
	);
//初始化
var locaVal = document.getElementById("map_local");
if(locaVal){
	ac.setInputValue(locaVal.value);
}

ac.addEventListener("onhighlight", function(e) {
	var str = "";
	var _value = e.fromitem.value;
	var value = "";
	if (e.fromitem.index > -1) {
		value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	}    
	str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
	
	value = "";
	if (e.toitem.index > -1) {
		_value = e.toitem.value;
		value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	}    
	str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
	document.getElementById("searchResultPanel").innerHTML = str;
});

var myValue;
ac.addEventListener("onconfirm", function(e) {
var _value = e.item.value;
	myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	document.getElementById("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
	
	setPlace();
});
var pp;
function setPlace(){
	map.clearOverlays();
	function myFun(){
		pp = local.getResults().getPoi(0).point;
		map.centerAndZoom(pp, 18);
		map.addOverlay(new BMap.Marker(pp));
		document.getElementById("ad_longitude").value=pp.lng;
		document.getElementById("ad_latitude").value=pp.lat;
	}
	var local = new BMap.LocalSearch(map, {
	  onSearchComplete: myFun
	});
	local.search(myValue);
}

function searchMap(){
	var lon = document.getElementById("ad_longitude").value;
	var lat = document.getElementById("ad_latitude").value;
	if(lon==""||lat==""){
		myCity.get(myFun);
	}else{
		var ra = document.getElementById('radius').value;
		initMarker(lon,lat,ra);
	}
}

function initMarker(lon,lat,radius){
	map.clearOverlays();
	if (radius>0) {
		var initPP = new BMap.Point(lon, lat);
		var circle = new BMap.Circle(initPP,radius,{fillColor:"blue", strokeWeight: 1 ,fillOpacity: 0.3, strokeOpacity: 0.3});
		map.addOverlay(new BMap.Marker(initPP));
		map.addOverlay(circle);
		if (radius>1000) {
			map.centerAndZoom(initPP, 14);
		}else{
			map.centerAndZoom(initPP, 18);	
		}
	}
}

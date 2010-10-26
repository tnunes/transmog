function submitSearch(myfield, e) {
	var keycode;
	if (window.event)
		keycode = window.event.keyCode;
	else if (e)
		keycode = e.which;
	else
		return true;
	if (keycode == 13) {
		//alert("myfield.value->"+myfield.value);
		var location = 'http://localhost:8080/conceptstore-manager/query/'+myfield.value+".html";
		window.location.href = location;
		return false;
	} else
		return true;
}

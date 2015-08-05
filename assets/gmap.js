var map = new google.maps.Map(document.getElementById('map'), {
	zoom: 12,
	center: new google.maps.LatLng(47.071876, 15.441456),
	mapTypeId: google.maps.MapTypeId.ROADMAP
});

var locations = [
	['Stadtbibliothek Zanklhof', 47.06976, 15.43154, 1],
	['Stadtbibliothek dieMediathek', 47.06975, 15.43116, 2],
	['Stadtbibliothek Gösting', 47.09399, 15.40548, 3],
	['Stadtbibliothek Graz West', 47.06993, 15.40727, 4],
	['Stadtbibliothek Graz Ost', 47.06934, 15.45888, 5],
	['Stadtbibliothek Graz Süd', 47.04572, 15.43234, 6],
	['Stadtbibliothek Graz Nord', 47.08350, 15.43212, 7],
	['Stadtbibliothek Andritz', 47.10280, 15.42137, 8]
];

var infowindow = new google.maps.InfoWindow();

var marker, i;
for (i = 0; i < locations.length; i++) {
	marker = new google.maps.Marker({
		position: new google.maps.LatLng(locations[i][1], locations[i][2]),
		map: map
	});
	google.maps.event.addListener(marker, 'click', (function(marker, i) {
		return function() {
			infowindow.setContent(locations[i][0]);
			infowindow.open(map, marker);
		}
	})(marker, i));
}

function init() {
	var o = document.getElementById("map");
	var t = document.getElementById("test");
	o.style.height = screen.availHeight + "px";
	o.style.width = screen.availWidth + "px";
	t.innerText = screen.availWidth + 'x' + screen.availHeight;
}
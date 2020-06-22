function markPOI(message){
    document.getElementById("map").innerText = message;
}

function drawRectangle(startLatitude, startLongitude, endLatitude, endLongitude, borderWeight, borderColor, borderAlpha, style, fillColor, fillAlpha){
    var sw = new kakao.maps.LatLng(startLatitude, startLongitude), // 사각형 영역의 남서쪽 좌표
        ne = new kakao.maps.LatLng(endLatitude,  endLongitude); // 사각형 영역의 북동쪽 좌표
    var rectangleBounds = new kakao.maps.LatLngBounds(sw, ne);
    var rectangle = new kakao.maps.Rectangle({
        bounds: rectangleBounds,
        strokeWeight: borderWeight, // int
        strokeColor: borderColor, // #000000
        strokeOpacity: borderAlpha, // float
        strokeStyle: style, // dashed, solid, shortdashdot, longdash
        fillColor: fillColor, // #000000
        fillOpacity: fillAlpha  // float
    });
    rectangle.setMap(map);
}

function drawCircle(latitude, longitude, radius, borderWeight, borderColor, borderAlpha, style, fillColor, fillAlpha){
    var circle = new kakao.maps.Circle({
        center : new kakao.maps.LatLng(latitude, longitude),
        radius: radius, // 미터 단위
        strokeWeight: borderWeight, // int
        strokeColor: borderColor, // #000000
        strokeOpacity: borderAlpha, // float
        strokeStyle: style, // dashed, solid, shortdashdot, longdash
        fillColor: fillColor, // #000000
        fillOpacity: fillAlpha  // float
    });
    circle.setMap(map);
}

function drawLine(latitudeList, longitudeList, borderWeight, borderColor, borderAlpha, style) {
    var linePath = [
        new kakao.maps.LatLng(33.452344169439975, 126.56878163224233),
        new kakao.maps.LatLng(33.45178067090639, 126.5726886938753)
    ];
    var polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: borderWeight, // int
        strokeColor: borderColor, // #000000
        strokeOpacity: borderAlpha, // float
        strokeStyle: style, // dashed, solid, shortdashdot, longdash
    });
    polyline.setMap(map);
}

function drawPolygon(latitudeList, longitudeList, latitudeList, longitudeList, borderWeight, borderColor, borderAlpha, style) {
    var polygonPath = [
        new kakao.maps.LatLng(33.45133510810506, 126.57159381623066),
        new kakao.maps.LatLng(33.44955812811862, 126.5713551811832),
        new kakao.maps.LatLng(33.449986291544086, 126.57263296172184),
        new kakao.maps.LatLng(33.450682513554554, 126.57321034054742),
        new kakao.maps.LatLng(33.451346760004206, 126.57235740081413)
    ];
    var polygon = new kakao.maps.Polygon({
        path:polygonPath,
        strokeWeight: borderWeight, // int
        strokeColor: borderColor, // #000000
        strokeOpacity: borderAlpha, // float
        strokeStyle: style, // dashed, solid, shortdashdot, longdash
        fillColor: fillColor, // #000000
        fillOpacity: fillAlpha  // float
    });
    polygon.setMap(map);
}

function createInfoWindow(latitude, longitude, message){
    var iwContent = '<div style="padding:5px;">' + message + '</div>',
        iwPosition = new kakao.maps.LatLng(latitude, longitude),
        iwRemoveable = true;

    var infowindow = new kakao.maps.InfoWindow({
        map: map,
        position : iwPosition,
        content : iwContent,
        removable : iwRemoveable
    });
}
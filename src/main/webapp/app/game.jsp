<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <title>Game</title>
</head>
<body onload="checkStatus()">
<div id="placement-field1" class="w3-hide w3-cell-row">
    <div class="w3-container w3-cell">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td><input type="radio" id="${col}${row}" onchange="cellClicked('${col}${row}')"/></td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
    <div class="w3-container w3-cell">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td></td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
    <div class="w3-container w3-cell w3-cell-bottom" ><button type="button" onclick="fire()">Fire!</button></div>
</div>
<div id="placement-field2" class="w3-hide w3-cell-row">
    <div class="w3-container w3-cell">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td></td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
    <div class="w3-container w3-cell">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td></td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
</div>
<script>
    var data = {};
    function cellClicked(id) {
        var checkbox = document.getElementById(id);
        console.log(id + " " + checkbox.checked);
        data[id] = checkbox.checked ? "SHIP" : "EMPTY";
    }
    function fire() {
        console.log("checking status");
        fetch("<c:url value='/api/game/fire'/>", {
            "method": "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            console.log("DONE");
            checkStatus();
        });
    }
    function checkStatus() {
        console.log("checking status");
        fetch("<c:url value='/api/game/status'/>", {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (game) {
            console.log(JSON.stringify(game))
            if (game.playerActive) {
                document.getElementById("placement-field1").classList.remove("w3-hide");
                document.getElementById("placement-field2").classList.add("w3-hide");
            } else {
                document.getElementById("placement-field1").classList.add("w3-hide");
                document.getElementById("placement-field2").classList.remove("w3-hide");
                window.setTimeout(function () {
                    checkStatus();
                }, 1000);
            }
        });
    }
</script>
</body>
</html>
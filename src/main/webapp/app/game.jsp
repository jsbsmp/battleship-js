<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <title>Game</title>
</head>
<body onload="checkStatus(); init();">
<div id="placement-field1" class="w3-hide w3-row">
    <div class="w3-col" style="width:400px">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td class="w3-panel w3-border w3-padding-small"><input type="radio" id="${col}${row}"
                                                                               onchange="cellClicked('${col}${row}')"/>
                        </td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
    <div class="w3-rest">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td class="w3-panel w3-border w3-padding-small" id="${col}${row}2"
                            ></td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
    <div class="w3-row">
        <button type="button" onclick="fire()">Fire!</button>
    </div>
</div>
<div id="placement-field2" class="w3-hide w3-row">
    <div class="w3-col" style="width:400px">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td class="w3-panel w3-border w3-padding-small"></td>
                    </c:forTokens>
                </tr>
            </c:forTokens>
        </table>
    </div>
    <div class="w3-rest">
        <table>
            <tr>
                <td>&nbsp;</td>
                <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${col}"/></td>
                </c:forTokens>
            </tr>
            <c:forTokens items="1,2,3,4,5,6,7,8,9,10" delims="," var="row">
                <tr>
                    <td class="w3-panel w3-border w3-padding-small"><c:out value="${row}"/></td>
                    <c:forTokens items="A,B,C,D,E,F,G,H,I,J" delims="," var="col">
                        <td class="w3-panel w3-border w3-padding-small"></td>
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

    function init() {
        var i;
        var j;
        var address;
        var alphabets = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
        for (i = 1; i < 11; i++) {
            for (j = 0; j < alphabets.length; j++) {
                address = alphabets[j] + i;
                checkCellState(address);
            }
        }
    }

    function checkCellState(address) {
        console.log("checking cell state");
        fetch("<c:url value='/api/game/state/'/>" + address, {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (cellstate) {
            console.log("===> JSON.stringify(cellstate): " + JSON.stringify(cellstate));
            console.log("===> cellstate.state: " + cellstate.state);
            if (cellstate.state === "SHIP") {
                document.getElementById(address + "2").classList.add("w3-green");
            }
        });
    }
</script>
</body>
</html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
    <title>Game</title>
    <style>
        td.SHIP {
            background-color: lightgreen;
        }
        td.MISS {
            background-color: lightblue;
        }
        td.HIT {
            background-color: darkred;
        }
    </style>
</head>
<body onload="checkStatus()">
<div id="wait-another" class="w3-hide">
    <h1>Please wait for another player move</h1>
</div>
<div id="make-move" class="w3-hide">
    <h1>Make your move</h1>
</div>
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
                    <td id="t${col}${row}" class="w3-panel w3-border w3-padding-small">
                        <input name="address" type="radio" id="${col}${row}"/></td>
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
                    <td id="m${col}${row}" class="w3-panel w3-border w3-padding-small"></td>
                </c:forTokens>
            </tr>
        </c:forTokens>
    </table>
</div>
<div id="select-fire" class="w3-hide">
    <button type="button" onclick="fire()">Fire!</button>
</div>
<script>

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
            console.log(JSON.stringify(game));
            if (game.status === "STARTED" && game.playerActive) {
                document.getElementById("wait-another").classList.add("w3-hide");
                document.getElementById("make-move").classList.remove("w3-hide");
                document.getElementById("select-fire").classList.remove("w3-hide");
                setRadioButtonsVisible(true);
            } else if (game.status === "STARTED" && !game.playerActive) {
                document.getElementById("wait-another").classList.remove("w3-hide");
                document.getElementById("make-move").classList.add("w3-hide");
                document.getElementById("select-fire").classList.add("w3-hide");
                setRadioButtonsVisible(false);
                window.setTimeout(function () {
                    checkStatus();
                }, 1000);
            } else {
                return;
            }
            drawShips();
        });
    }

    function drawShips() {
        fetch("<c:url value='/api/game/cells'/>", {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (cells) {
            cells.forEach(function (c) {
                var id = (c.targetArea ? "t" : "m") + c.address;
                var tblCell = document.getElementById(id);
                tblCell.className = c.state;
            });
        });
    }

    function setRadioButtonsVisible(visible) {
        var radioButtons = document.querySelectorAll('input[name=address]');
        radioButtons.forEach(function (btn) {
            if (visible) {
                btn.classList.remove("w3-hide");
            } else {
                btn.classList.add("w3-hide");
            }
        });
    }

    function fire() {
        console.log("firing");
        var checked = document.querySelector('input[name=address]:checked');
        var checkedAddr = checked.id;
        console.log("firing address " + checkedAddr);
        fetch("<c:url value='/api/game/fire'/>/" + checkedAddr, {
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

</script>
</body>
</html>
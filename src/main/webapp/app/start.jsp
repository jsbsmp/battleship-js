<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<title>Title</title>
<script src="http://www.w3schools.com/lib/w3data.js"></script>
<body onload="displayWinners()">
<button type="button" onclick="logout()">Log out</button>
<button type="button" onclick="startGame()">Start Game</button>
<br><br>

<table id="winners">
    <tr>
        <td>
            <hi>Winners</hi>
        </td>
    </tr>
    <tr>
        <th>Username</th>
        <th>Moves</th>
    </tr>
    <tr w3-repeat="winners">
        <td>{{winnerName}}</td>
        <td>{{winnerMoves}}</td>
    </tr>
</table>

<script>

    function logout() {
        fetch("<c:url value='/api/auth/logout'/>", {"method": "POST"})
            .then(function (response) {
                location.href = "/";
            });
    }

    function startGame() {
        fetch("<c:url value='/api/game'/>", {"method": "POST"})
            .then(function (response) {
                location.href = "/app/placement.jsp";
            });
    }


    function displayWinners() {
        fetch("<c:url value="/api/game/winners"/>", {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (winners) {
            console.log(JSON.stringify(winners));
            w3DisplayData("winners", winners);
        });
    }

</script>
</body>
</html>

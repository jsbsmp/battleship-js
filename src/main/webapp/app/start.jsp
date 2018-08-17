<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<title>Title</title>
<script src="http://www.w3schools.com/lib/w3data.js"></script>
<body onload="getFinishedGames()">
<button type="button" onclick="logout()">Log out</button>
<button type="button" onclick="startGame()">Start Game</button>
<br><br>
<div id="error-panel" class="w3-panel w3-red w3-hide">
    <h3>Error!</h3>
    <p>{{message}}</p>
</div>





<script>




    function showError(msg) {
        var errorPanel = document.getElementById("error-panel");
        errorPanel.classList.remove("w3-hide");
        w3DisplayData("error-panel", {"message" : msg});
    }

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

    function getFinishedGames() {
        fetch("<c:url value='/api/game/games'/>", {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (games) {
            games.forEach(function (g) {
                console.log(g.winner);
                console.log(g.winnerMoves)
                showError(g.winner);
            });
        });
    }

    function printWinners() {
        fetch("<c:url value='/api/game/winners'/>", {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (winners) {
            winners.forEach(function (u) {
                console.log(u.username);
                console.log(u.victories);


                showError(u.username);

            });
        });
    }

</script>
</body>
</html>

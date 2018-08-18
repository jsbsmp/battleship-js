<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Game Over</title>
</head>
<script src="http://www.w3schools.com/lib/w3data.js"></script>
</html>
<body onload="showWinner()">
<label>The winner is: </label><br>
<div id="message-panel">
    <p>{{message}}</p>
</div>
<br><br>
<a href="start.jsp">Start a new game</a>
<script>

    function showWinner() {
        fetch("<c:url value='/api/game/user'/>", {
            "method": "GET",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(function (response) {
            return response.json();
        }).then(function (user) {
            w3DisplayData("message-panel", {"message": user.username});
        });
    }

</script>

</body>
</html>

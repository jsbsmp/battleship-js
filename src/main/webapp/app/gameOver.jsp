<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Game Over</title>
</head>
</html>
<body onload="showWinner()">
<label>The winner is: </label><br>
<input type="text"/><br>
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
            document.querySelector('input').setAttribute("value", user.username);
        });
    }
</script>

</body>
</html>

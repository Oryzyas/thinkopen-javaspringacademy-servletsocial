<html>
<head>
    <title>Signup</title>
</head>
<body>
    <form name="signup" method="post" action="/signup">
        <table width="500px" height="500px" align="center">
            <tr>
                <td><label for="name">Name</label> </td>
                <td><input type="text" name="name" size="50"/></td>
            </tr>
            <tr>
                <td><label for="email">E-mail</label> </td>
                <td><input type="text" name="email" size="30"/></td>
            </tr>
            <tr>
                <td><label for="age">Age</label> </td>
                <td><input type="number" name="age" min="18" max="100" value="18"/></td>
            </tr>
            <tr>
                <td><label for="password">Password</label> </td>
                <td><input type="password" name="password" size="20"/></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align:center">
                    <input type="submit" value="Submit" />
                </td>
            </tr>
        </table>
    </form>
</body>
</html>

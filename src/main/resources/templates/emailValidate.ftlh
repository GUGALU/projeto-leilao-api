<p>Bem-vindo, <span th:text="${name}"></span>!</p>
<p>Clique no link abaixo para validar seu cadastro:</p>
<a th:href="@{'http://localhost:8080/validate?email=' + ${email} + '&token=' + ${token}}">Validar Cadastro</a>

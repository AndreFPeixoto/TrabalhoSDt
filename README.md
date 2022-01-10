# TrabalhoSDt
O Loader é um servidor sftp, ou seja, para que seja possivel conectar com o servidor é necessário criar um certificado.
Para isso basta abrir o Loader no Intellij e executar o programa.
Durante a execução, é necessário abrir o terminal e introduzir o comando
> sftp -oPort=2000 test@localhost 

Após confirmar, introduza a password 'password'.

Se aparecer a prompt 'sftp>' o programa está a funcionar e pronto para receber pedidos.

Relembro ainda que para que tudo funcione da forma correta, tanto o Loader quanto o Stabilizer devem estar a correr antes de executar o Client.

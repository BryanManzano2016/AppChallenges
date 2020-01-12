# AppChallenges
Cliente y servidor para la aplicacion AppChallenges

# Consideraciones para backend

* Ejecutar los archivos .sql en mysql para cargar esquema y datos

* Instalar driver mysql mediante consola con ---> go get -u github.com/go-sql-driver/mysql

* Ejecutar en consola, esto dentro de la carpeta donde esta el archivo mediante:  ---> go run challengesServidor.go

* Adaptar la variable global "configuracionMysql" al formato ---> user:password@tcp(127.0.0.1:3306)/database.
Es asi que si su usuario es root y contraseña es 123 deberia ser:  root:123@tcp(127.0.0.1:3306)/database.

* Abrir el puerto 9000 en el sistema. En windows mediante agregar una regla de entrada al firewall

* Las fotos de validacion de servicios estan en la carpeta imagenesValidacion

# Consideraciones para frontEnt

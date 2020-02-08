# AppChallenges
Cliente y servidor para la aplicacion AppChallenges. Teniendo al lenguaje Golang como backend y ... como frontEnd

Video demostrativo de FrontEnd: https://www.youtube.com/watch?v=ZSEhCNHrKIs&feature=youtu.be

# Consideraciones para backend

* Ejecutar los archivos .sql en mysql para cargar esquema y datos

* Instalar driver mysql mediante consola con ---> go get -u github.com/go-sql-driver/mysql . 

* Ejecutar en consola, esto dentro de la carpeta donde esta el archivo mediante:  ---> go run challengesServidor.go

* Dependiendo la ip de la maquina donde se ejecuta el servidor deben cambiar la variable ip global, ya que el cliente movil estara en otro dispositivo y no puede conectarse al localhost.

* Adaptar la variable global "configuracionMysql" al formato ---> user:password@tcp(127.0.0.1:3306)/database.
Es asi que si su usuario es root y contraseña es 123 deberia ser:  root:123@tcp(127.0.0.1:3306)/database.

* Abrir el puerto 9000 en el sistema. En windows mediante agregar una regla de entrada al firewall.

* Las fotos de validacion de servicios estan en la carpeta imagenesValidacion

# Consideraciones para frontEnt

* Para conectar con el servidor localmente hay que editar la linea del archivo FrontEnd/Tester/app/src/main/res/xml/network_security_config.xml donde esta \<domain includeSubdomains="true">192.168.100.133\</domain>. Es asi que dependiendo donde este ejecutandose el servidor ese paramentro con la ip cambia.

* En la variable global de la clase Auxiliar cambiar a la ip del servidor, esto en val direccion_ip= "http://192.168.100.81:9000/"

* Como informacion el proyecto android utiliza las librerias okhttp para cuestiones de solicitudes al servidor y corutines para los procesos en 2do plano de kotlin y android. Esto se reflejara en el archivo build.gradle. Por otra parte, hay agregados permisos en el AndroidManifest.xml para la conexion a red.


package main

import (
	"database/sql"
	"encoding/json"
	"log"
	"net/http"

	_ "github.com/go-sql-driver/mysql" // DEBEN INSTALAR EL DRIVER MEDIANTE: go get -u github.com/go-sql-driver/mysql
)

/*
	Para que json reconozca los campos debe cada campo empezar con
 	mayuscula y se guardara como esta en los comentarios. ejm:   `json:"nombre"`.
*/

type Challenge struct {
	Nombre    string `json:"Nombre"`
	URL       string `json:"url"`
	Info      string `json:"Info"`
	Categoria string `json:"Categoria"`

}

type Grupos struct {
	NombreGrupo string `json:"GrupoNombre"`
	DESCRIPCION string `json:"Descripcion"`
	group_challenges_ID string `json:"CodeGrupo"`
	date_creation string `json: "FechaGrupo"`
	url_wp string `json:"url_whatsapp"`
}

// !!! user:password@tcp(127.0.0.1:3306)/database ¡¡¡
var configuracionMysql = "root:@tcp(127.0.0.1:3306)/groupchallenges"
var puertoServidor = "9000"

func main() {
	// Ejecutar en consola:                    go run challengesServidor.go
	// Colocar en el navegador de esta forma:    http://localhost:9000/path
	// http.HandleFunc("/path", funcionManejadora)
	http.HandleFunc("/obtenerChallenges", obtenerChallenges)
	http.HandleFunc("/obtenerChallengesCategoria", obtenerChallengesCategoria)
	http.HandleFunc("/unirseChallenge", unirseChallenge)
	http.HandleFunc("/unirseGrupo", unirseGrupo)
	http.HandleFunc("/obtenerGruposChallenges",obtenerGruposChallenges)	
	http.HandleFunc("/MeEncantaChallenge", MeEncantaChallenge)
	http.HandleFunc("/GruposEstudiante",GruposEstudiante)
	http.HandleFunc("/crearGrupo",crearGrupo)
	// En lugar de localhost puede ir la ip del servidor. Ademas es obligatorio desbloquear el puerto 9000
	log.Fatal(http.ListenAndServe("localhost:"+puertoServidor, nil))
}

// 1 -> servidor caido 			2 -> operacion exitosa 			3 -> operacion fallida
func mensajeFalloServidor(mensaje int) map[string]int {
	var respuesta map[string]int
	if mensaje == 1 {
		respuesta = map[string]int{"respuesta": 0}
	} else if mensaje == 2 {
		respuesta = map[string]int{"respuesta": 1}
	} else if mensaje == 3 {
		respuesta = map[string]int{"respuesta": 2}
	}
	return respuesta
}

// SERVICIOS
func obtenerChallenges(w http.ResponseWriter, r *http.Request) {

	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()
		// Usar stored procedure sin paramentros
		results, err := db.Query("call verChallenges()")

		if err != nil {
			// Como es error backend enviara 1 como respuesta
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else {

			// Arreglo con 0 elementos
			var lista = make([]Challenge, 0)
			// Recorre el resultado de consulta
			for results.Next() {

				var nombre string
				var url string
				// Guardar los campos en las variables
				err = results.Scan(&nombre, &url)
				// Crear el struct y luego añadir al array
				challenge := Challenge{Nombre: nombre, URL: url}
				lista = append(lista, challenge)

				if err != nil {
					// Igualmente, como es error backend enviara 1 como respuesta
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				}
			}
			// Codifica y responde al cliente de una vez
			json.NewEncoder(w).Encode(lista)
		}
	}
}

// Dada una categoria se recuperan los challenges
func obtenerChallengesCategoria(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()

		// ESTOS VALORES POR EL MOMENTO SERAN DADOS, LUEGO EL CLIENTE LOS OTORGA
		jsonData := []byte(`{"id":"Ciencia", "dia":"martes"}`)
		var data map[string]interface{}
		// Unmarshal(fuenteDatos, seAlmacena) es para decodificar
		json.Unmarshal(jsonData, &data)

		resultados, err := db.Query("call challengesCategoria(?)", data["id"].(string))

		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else {

			var lista = make([]Challenge, 0)
			for resultados.Next() {

				var nombre string
				var url string
				err = resultados.Scan(&nombre, &url)

				challenge := Challenge{Nombre: nombre, URL: url}
				lista = append(lista, challenge)

				if err != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				}
			}
			// De fondo Encode usa Marshall para crear el json
			json.NewEncoder(w).Encode(lista)
		}
	}
}

// Cuando cliente da click en unirse a challenge
func unirseChallenge(w http.ResponseWriter, r *http.Request) {
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {

		defer db.Close()
		// ESTOS VALORES POR EL MOMENTO SERAN DADOS, LUEGO EL CLIENTE LOS OTORGA
		jsonData := []byte(`{"idChallenge":9, "idEstudiante":4}`)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		resultados, err := db.Query("call CreatorConfirmation(?, ?)", data["idChallenge"], data["idEstudiante"])

		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else {
			for resultados.Next() {
				var respuesta int
				err = resultados.Scan(&respuesta)
				if err != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				} else {
					if respuesta == 1 {
						json.NewEncoder(w).Encode(mensajeFalloServidor(2))
					} else {
						json.NewEncoder(w).Encode(mensajeFalloServidor(3))
					}
				}
				break // Solo necesita la 1ra fila del resultSet porque es un nro confirmando operacion
			}
		}
	}
}

// Cuando cliente da click a unirse a grupo
func unirseGrupo(w http.ResponseWriter, r *http.Request) {

	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()

		// ESTOS VALORES POR EL MOMENTO SERAN DADOS, LUEGO EL CLIENTE LOS OTORGA
		jsonData := []byte(`{"idGrupo":9, "idEstudiante":4}`)
		var data map[string]interface{}
		// Unmarshal(fuenteDatos, seAlmacena) es para decodificar
		json.Unmarshal([]byte(jsonData), &data)

		resultados, err := db.Query("call CreatorGroup_student(?, ?)", data["idGrupo"],
			data["idEstudiante"])

		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else {
			for resultados.Next() {
				var respuesta int
				err = resultados.Scan(&respuesta)
				if err != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				} else {
					if respuesta == 1 {
						json.NewEncoder(w).Encode(mensajeFalloServidor(2))
					} else {
						json.NewEncoder(w).Encode(mensajeFalloServidor(3))
					}
				}
				break
			}
		}
	}
}

func obtenerGruposChallenges(w http.ResponseWriter, r *http.Request){
	
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()
		jsonData := []byte(`{"idChallenge":2}`)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)
		results, err := db.Query("call gruposxChallenge(?)",data["idChallenge"])

		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))

		} else {
			var lista = make([]Grupos, 0)
			for results.Next() {
				var groupnombre string
				var url_wp string
				var description string
				err = results.Scan(&groupnombre, &url_wp, &description )
				grupos := Grupos{NombreGrupo: groupnombre, url_wp: url_wp, DESCRIPCION: description}
				lista = append(lista, grupos)

				if err != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				}
			}
			json.NewEncoder(w).Encode(lista)
		}
	}
}

func MeEncantaChallenge(w http.ResponseWriter, r *http.Request){
	db, err := sql.Open("mysql", configuracionMysql)
	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()
		jsonData:=[]byte(`{"idChallenge":3}`)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)
		results, err := db.Query("call MasEncanta(?)",data["idChallenge"])
		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else {
			for results.Next() {
				var respuesta int
				err = results.Scan(&respuesta)
				if err != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				} else {
					if respuesta == 1 {
						json.NewEncoder(w).Encode(mensajeFalloServidor(2))
					} else {
						json.NewEncoder(w).Encode(mensajeFalloServidor(3))
					}
				}
				break
			}
		}
	}
	
}

func GruposEstudiante(w http.ResponseWriter, r *http.Request){
	db, err := sql.Open("mysql", configuracionMysql)
	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()
		jsonData:=[]byte(`{"idEstudiante":1}`)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)
		results, err := db.Query("call CrearVistaGrupoChallenge(?)",data["idEstudiante"])
		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else {
			var lista = make([]Grupos, 0)
			for results.Next() {
				var group_challenges_ID string 
				var groupname string
				var description string
				var url_whatsapp string
				var date_creation string
				err = results.Scan(&groupname, &url_whatsapp, &description, &date_creation, &group_challenges_ID)
				grupos := Grupos{NombreGrupo: groupname, url_wp: url_whatsapp, DESCRIPCION: description, date_creation: date_creation, group_challenges_ID: group_challenges_ID}
				lista = append(lista,grupos)
				if err != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
					} 
				}
				json.NewEncoder(w).Encode(lista)
			}
		}	
}

func crearGrupo(w http.ResponseWriter, r *http.Request){
	db, err := sql.Open("mysql", configuracionMysql)
	if err != nil {
		json.NewEncoder(w).Encode(mensajeFalloServidor(1))
	} else {
		defer db.Close()
		jsonData:=[]byte(`{"nombreGrupo":"Amigos","date":"2006-01-02","Descripcion":"Somos los mejores al usar Golang", "url_wp":"www.whatsaap/2DSAADUYUDSAD..."}`)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)
		results, err := db.Query("call GruposExistente(?)",data["nombreGrupo"])
		if err != nil {
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
		} else{
			var respuesta int
			for results.Next() {
				err = results.Scan(&respuesta)
				break;
			}
			if respuesta ==0 {
				results1, err1 := db.Query("call CreatorGroup(?,?,?,?)",data["date"],data["nombreGrupo"],data["Descripcion"],data["url_wp"])
				if err1 != nil {
					json.NewEncoder(w).Encode(mensajeFalloServidor(1))
				} else {
					for results1.Next() {
						var respond int
						err = results1.Scan(&respond)
						if err1 != nil {
						json.NewEncoder(w).Encode(mensajeFalloServidor(1))
					} 
				}
			}
		}else if respuesta == 1{
			json.NewEncoder(w).Encode(mensajeFalloServidor(1))
			}	
		}
	}
}





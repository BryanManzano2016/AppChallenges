package main

import (
	"database/sql"
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"

	_ "github.com/go-sql-driver/mysql" // DEBEN INSTALAR EL DRIVER MEDIANTE: go get -u github.com/go-sql-driver/mysql
)

/*
	Para que json reconozca los campos debe cada campo empezar con
 	mayuscula y se guardara como esta en los comentarios. ejm:   `json:"nombre"`.
*/

type Challenge struct {
	Nombre    string `json:"nombre"`
	URL       string `json:"url"`
	Info      string `json:"info"`
	Categoria string `json:"categoria"`
	Tfavorite string `json:"meGustas"`
	Date_inicio string `json:"fechaInicio"`
}

type Grupos struct {
	NombreGrupo       string `json:"grupoNombre"`
	Descripcion       string `json:"descripcion"`
	GroupChallengesID string `json:"codeGrupo"`
	FechaCreacion     string `json: "fechaGrupo"`
	UrlWp             string `json:"url_whatsapp"`
}

type Respuesta struct {
	Resp int `json:"respuesta"`
}

// !!! user:password@tcp(127.0.0.1:3306)/database ¡¡¡
var configuracionMysql = "root:@tcp(127.0.0.1:3306)/groupchallenges"
var puertoServidor = "9000"
var ip = "192.168.200.11"

func main() {
	// Ejecutar en consola:                    go run challengesServidor.go
	// Colocar en el navegador de esta forma:    http://localhost:9000/path
	// http.HandleFunc("/path", funcionManejadora)
	http.HandleFunc("/obtenerChallenges", obtenerChallenges)
	http.HandleFunc("/obtenerChallengesCategoria", obtenerChallengesCategoria)
	http.HandleFunc("/unirseChallenge", unirseChallenge)
	http.HandleFunc("/unirseGrupo", unirseGrupo)
	http.HandleFunc("/obtenerGruposChallenges", obtenerGruposChallenges)
	http.HandleFunc("/meEncantaChallenge", meEncantaChallenge)
	http.HandleFunc("/gruposEstudiante", gruposEstudiante)
	http.HandleFunc("/crearGrupo", crearGrupo)
	http.HandleFunc("/eliminarGrupoChallenge", eliminarGrupoChallenge)
	http.HandleFunc("/menosEncantaChallenge", menosEncantaChallenge)
	http.HandleFunc("/crearChallenge", crearChallenge)

	// En lugar de localhost puede ir la ip del servidor. Ademas es obligatorio desbloquear el puerto 9000
	log.Fatal(http.ListenAndServe(ip+":"+puertoServidor, nil))
}

// 0 -> servidor caido 			1 -> operacion exitosa 			2 -> operacion fallida
func respuestaArreglo(mensaje int) []Respuesta {
	var lista = make([]Respuesta, 0)
	lista = append(lista, Respuesta{Resp: mensaje})
	return lista
}

func obtenerChallenges(w http.ResponseWriter, r *http.Request) {

	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()
		// Usar stored procedure sin paramentros
		resultados, err := db.Query("call verChallenges()")

		if err != nil {
			// Como es error backend enviara 1 como respuesta
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {

			// Arreglo con 0 elementos
			var lista = make([]Challenge, 0)
			// Recorre el resultado de consulta
			for resultados.Next() {

				var nombre string
				var url string
				var info string
				var categoria string
				var tfavorite string
				var date_inicio string
				// Guardar los campos en las variables
				err = resultados.Scan(&nombre, &url, &info, &categoria, &tfavorite, &date_inicio)
				// Crear el struct y luego añadir al array
				challenge := Challenge{Nombre: nombre, URL: url, Info: info,
					Categoria: categoria, Tfavorite: tfavorite, Date_inicio: date_inicio}
				lista = append(lista, challenge)

				if err != nil {
					// Igualmente, como es error backend enviara 1 como respuesta
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				}
			}
			// Codifica y responde al cliente de una vez
			json.NewEncoder(w).Encode(lista)
		}
	}
}

// Dada una categoria se recuperan los challenges
func obtenerChallengesCategoria(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		// ESTOS VALORES POR EL MOMENTO SERAN DADOS, LUEGO EL CLIENTE LOS OTORGA
		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		// Unmarshal(fuenteDatos, seAlmacena) es para decodificar
		json.Unmarshal(jsonData, &data)

		resultados, err := db.Query("call challengesCategoria(?)", data["categoria"].(string))

		if err != nil {
			// Como es error backend enviara 1 como respuesta
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {

			// Arreglo con 0 elementos
			var lista = make([]Challenge, 0)
			// Recorre el resultado de consulta
			for resultados.Next() {

				var nombre string
				var url string
				var info string
				var categoria string
				// Guardar los campos en las variables
				err = resultados.Scan(&nombre, &url, &info, &categoria)
				// Crear el struct y luego añadir al array
				challenge := Challenge{Nombre: nombre, URL: url, Info: info,
					Categoria: categoria}

				lista = append(lista, challenge)

				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				}
			}
			json.NewEncoder(w).Encode(lista)
		}
	}
}

// Cuando cliente da click en unirse a challenge
func unirseChallenge(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {

		defer db.Close()
		// ESTOS VALORES POR EL MOMENTO SERAN DADOS, LUEGO EL CLIENTE LOS OTORGA
		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		resultados, err := db.Query("call CreatorConfirmation(?, ?)", data["idChallenge"], data["idEstudiante"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for resultados.Next() {
				var respuesta int
				err = resultados.Scan(&respuesta)
				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break // Solo necesita la 1ra fila del resultSet porque es un nro confirmando operacion
			}
		}
	}
}

// Cuando cliente da click a unirse a grupo
func unirseGrupo(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		// ESTOS VALORES POR EL MOMENTO SERAN DADOS, LUEGO EL CLIENTE LOS OTORGA
		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		// Unmarshal(fuenteDatos, seAlmacena) es para decodificar
		json.Unmarshal([]byte(jsonData), &data)

		resultados, err := db.Query("call CreatorGroup_student(?, ?)", data["idGrupo"],
			data["idEstudiante"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for resultados.Next() {
				var respuesta int
				err = resultados.Scan(&respuesta)

				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break // Solo necesita la 1ra fila del resultSet porque es un nro confirmando operacion
			}
		}

	}
}

func obtenerGruposChallenges(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()
		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)
		results, err := db.Query("call gruposxChallenge(?)", data["idChallenge"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			var lista = make([]Grupos, 0)
			for results.Next() {
				var grupoNombre string
				var urlWp string
				var descripcion string

				err = results.Scan(&grupoNombre, &urlWp, &descripcion)
				grupos := Grupos{NombreGrupo: grupoNombre, UrlWp: urlWp, Descripcion: descripcion}
				lista = append(lista, grupos)

				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				}
			}
			json.NewEncoder(w).Encode(lista)
		}
	}
}

func meEncantaChallenge(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		resultados, err := db.Query("call MasEncanta(?)", data["idChallenge"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for resultados.Next() {
				var respuesta int
				err = resultados.Scan(&respuesta)

				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break // Solo necesita la 1ra fila del resultSet porque es un nro confirmando operacion
			}
		}
	}
}

func gruposEstudiante(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		results, err := db.Query("call gruposEstudiante(?)", data["idEstudiante"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			var lista = make([]Grupos, 0)
			for results.Next() {
				var groupChallengesID string
				var nombreGrupo string
				var descripcion string
				var urlWhatsapp string

				err = results.Scan(&nombreGrupo, &descripcion, &urlWhatsapp, &groupChallengesID)

				grupo := Grupos{NombreGrupo: nombreGrupo, UrlWp: urlWhatsapp, Descripcion: descripcion,
					GroupChallengesID: groupChallengesID}

				lista = append(lista, grupo)
				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				}
			}
			json.NewEncoder(w).Encode(lista)
		}
	}
}

func crearGrupo(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		results1, err1 := db.Query("call CreatorGroup(?,?,?)", data["nombreGrupo"], data["Descripcion"], data["url_wp"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for results1.Next() {
				var respuesta int
				err1 = results1.Scan(&respuesta)
				if err1 != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break
			}
		}
	}
}

func eliminarGrupoChallenge(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		resultados, err := db.Query("call EliminarGroup_student(?, ?)", data["idGrupo"],
			data["idEstudiante"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for resultados.Next() {
				var respuesta int
				err = resultados.Scan(&respuesta)
				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break
			}
		}
	}
}

func menosEncantaChallenge(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		results, err := db.Query("call MenosEncanta(?)", data["idChallenge"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for results.Next() {
				var respuesta int
				err = results.Scan(&respuesta)
				if err != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break
			}
		}
	}
}

func crearChallenge(w http.ResponseWriter, r *http.Request) {

	jsonRecibido, err2 := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	db, err := sql.Open("mysql", configuracionMysql)

	if err != nil || err2 != nil {
		json.NewEncoder(w).Encode(respuestaArreglo(0))
	} else {
		defer db.Close()

		jsonData := []byte(jsonRecibido)
		var data map[string]interface{}
		json.Unmarshal([]byte(jsonData), &data)

		results1, err1 := db.Query("call CreatorChallange(?,?,?,?)", data["nombreChallenge"], data["Categoria"],
			data["info"], data["url_img"])

		if err != nil {
			json.NewEncoder(w).Encode(respuestaArreglo(0))
		} else {
			for results1.Next() {
				var respuesta int
				err1 = results1.Scan(&respuesta)
				if err1 != nil {
					json.NewEncoder(w).Encode(respuestaArreglo(0))
				} else {
					json.NewEncoder(w).Encode(respuestaArreglo(respuesta))
				}
				break
			}
		}
	}
}

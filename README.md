## Credenciales
### Usuario con permiso de paciente

Email: patienttest@medicarepaw.com.ar

Contraseña: 12345678


### Usuario con permiso de médico
Email: barteaga@medicarepaw.com.ar

Contraseña: Dolores1

## Deployment local
### Archivos .properties y .env
Para poder configurar correctamente los paths de la aplicacion y otras configuraciones para un deployment local es necesario crear varios archivos.
La aplicacion esta configurada para que primero tome la version local de estos archivos y, si no es encontrada,
tome la version de produccion (que es el mismo nombre terminado en '-prod.properties' para el caso del properties o '.env' a secas para el caso de los env de Vue).
Esto permite que se puedan crear archivos con configuracoin local y no tener que modificar datos de acceso a nivel de produccion.
Por este motivo, los mismos no deberian de ser versionados.

#### Java
En el caso de Java es necesario crear un archivo 'application-local.properties' con las siguientes propiedades:

```properties
# Host donde correra nuestra aplicacion. Esto afecta tanto las 
# cookies creadas como los links enviados por mail
app.host=localhost
# Path donde corre la API (en el host)
app.api.path=/api

# Datos de acceso a la base de datos
db.host=localhost
db.port=5433
db.user=postgres
db.pass=postgres
db.db=paw
```

**_Importante_**: Debido a que tanto el JWT como el Refresh Token fueron implementados para aumentar la seguridad del sistema, la autenticacion NO funcionara si el app.host lleva especificado un puerto y/o path diferente a donde corre Vue.

Por ejemplo, si Spring corre en 'localhost:9090/medicare' y Vue en 'localhost:8081/' 
(y por lo tanto tendremos seteado el app.host en 'localhost:9090/medicare')
entonces el cookie del JWT, que tendra su propiedad "Path" seteada en el valor del "app.host"
NO sera seteada por el cliente (por el browser) ya que el mismo no permite setear cookies
especificados para un dominio diferente del cual salio la request.

La unica solucion (que es mas bien un workaround) se trata de setear el app.host en 'localhost' (a secas),
aunque esto trae el problema de que la url de los mails no sera la correcta. Sin embargo, a los demas efectos
de la aplicacion seguira funcionando


#### Vue
En el caso de Vue es necesario crear un archivo '.env.local' con las siguientes entradas

 ```dotenv
# Host donde se encuentra el servidor. Todas las llamadas que se hacen en Vue
# que no corresponden a alguna ruta .html sera proxeada a esta direccion
# como lo son las llamadas a la API.
LOCAL_PROXY_SERVER = 'http://localhost:9090/'

# Este es el path donde se encuentra la API (en el proxy server)
VUE_APP_API_PATH = '/api'

# Donde se encuentran los .html (en el servidor donde corre Vue, no el proxy server)
VUE_APP_ROOT_PATH = '/'
```
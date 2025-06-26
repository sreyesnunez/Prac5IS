# 🌍 ProyectoFinal - Sistema de Consulta de Sismos

Este proyecto es una aplicación web desarrollada con **Spring Boot** que permite a los usuarios registrarse, iniciar sesión, editar su perfil, consultar información y simular sismos en un mapa interactivo. Está orientado a brindar una interfaz sencilla y útil para visualizar eventos sísmicos ocurridos en México.

## 🚀 Tecnologías utilizadas

- Java 17  
- Spring Boot  
- Spring Security  
- Thymeleaf  
- MySQL (contenedorizado)  
- Maven  
- HTML + CSS (con soporte para tema claro/oscuro)  
- Leaflet.js (para el mapa interactivo)  
- Docker + Docker Compose

## 🧑‍💻 Funcionalidades principales

- Registro y login de usuarios con roles (`USER`, `ADMIN`)  
- Edición de perfil de usuario (nombre, correo, contraseña)  
- Selector de tema claro/oscuro con persistencia en base de datos  
- Visualización de sismos en un mapa con marcadores personalizados  
- Simulador de sismos en un mapa con animación  
- CRUD de usuarios e incidencias para administradores  

---

## ⚙️ Instalación y ejecución rápida con Docker

### 1. Clona el repositorio

```bash
git clone https://github.com/tu-usuario/ProyectoFinal.git
cd ProyectoFinal

```

### 2. Ejecuta el sistema

Asegúrate de tener Docker y Docker Compose instalados. Luego ejecuta:

```bash
docker-compose up --build
```

### 3. Accede a la aplicación 

Una vez levantado el contenedor, abre tu navegador y ve a:

```
http://localhost:8080/login
```

---

## 👤 Credenciales de prueba

**Administrador**  
- Correo: sebas@gmail.com  
- Contraseña: sebas

---


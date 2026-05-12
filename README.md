# Nombre de la aplicación

### TaikaiSensei

Proyecto de Fin de Grado Superior – Desarrollo de Aplicaciones Multiplataforma (DAM)

---

### Descripción

Aplicación desarrollada para prevenir la generación de residuos, mejorar el impacto ambiental, fomentar la sostenibilidad y eficiencia, predicar con la economía circular y cambiar hábitos a las organizaciones y personas que utilicen la aplicación.

---

### Objetivo

El objetivo principal del proyecto es digitalizar la gestión de competiciones de karate, actuando como herramienta de borrador para trabajadores y organizadores, con el fin de reducir el uso de papel y optimizar la preparación de eventos deportivos.

La aplicación permite crear y gestionar cuadros de competición en fase de preparación, consultar un historial de resultados y reutilizar información de torneos anteriores de forma estructurada. Además, incorpora un marcador digital destinado a entrenamientos y competiciones de kumite de menor escala.

Con ello se busca mejorar la eficiencia organizativa, reducir el impacto ambiental y fomentar la adopción de procesos más sostenibles mediante la digitalización.

---

### Tecnologías utilizadas

* Kotlin
* Android Studio
* Firebase

---

### Funcionalidades principales

* Crear Diagramas de competición
* Consultas al Historial de Diagramas anteriores
* Marcador regulado para la modalidad de Kumite

---

### Instalación y ejecución

```bash
git clone https://github.com/Ikerxo/Trabajo-Final-Iker-Moreno-Lopez.git

cd Trabajo-Final-Iker-Moreno-Lopez
```

Abrir el proyecto en Android Studio y ejecutar en un emulador o dispositivo físico.

---

#### Centro

IES LAS SALINAS

#### Curso

2º DESARROLLO DE APLICACIONES MULTIPLATAFORMA (DAM)

#### Autor

Iker Moreno López



## Guía de uso para TaikaiSensei

### 1. Inicio de sesión

En la primera instancia de la aplicación se encuentra el inicio de sesión, aquí se deberá acceder con las credenciales de acceso de un usuario previamente autorizado por el administrador (correo electrónico y contraseña)

A modo de prueba, se pueden utilizar las siguientes credenciales:


- Correo: t1taikaisensei@gmail.com 
- Contraseña: tribunal1

  
- Correo: t2taikaisensei@gmail.com
- Contraseña: tribunal2

  
Verificadas las credenciales, se accederá a la home de la aplicación.

###  2. Home

En la pantalla Home el usuario podrá acceder a las diversas funciones de la aplicación pulsando en la opción que desee:

1. - Crear Nuevo Diagrama: En esta el usuario podrá gestionar una categoría como borrador de principio a fin.
  
2. - Ver Historial de Torneos: En esta el usuario podrá consultar los resultados de las distintas categorías que haya ido creando a lo largo del evento.

3. - Marcador Kumite: En esta el usuario podrá manipular un marcador para la modalidad de Kumite totalmente reglamentario a la normativa vigente.
  

#### 2.1 Crear Nuevo Diagrama

Esta función cuenta con varias pantallas, para comenzar nos pedirá un nombre para el torneo y un nombre para la categoría ("Campeonato de España", "Junior Masculino >81kg"), después nos solicitará en que ronda comienza la categoría ("dieciseisavos""octavos""cuartos""semis"), la siguiente pantalla solicitará el nombre de los competidores y el nombre del club por el que compiten en el orden que tenga la categoría oficial ("Iker""Nisseishi"), una vez hecho esto la aplicación generará un listado con todos los combates en el que se deberá elegir en cada enfrentamiento si gana Aka (Rojo) o Ao (Azul), una vez realizados todos los enfrentamientos se deberá continuar hasta declarar un campeón y un subcampeón, estos datos se guardarán junto con los datos del torneo a nombre de la cuenta con la que se está accediendo a la aplicación.

#### 2.2 Ver Historial de Torneos

Esta función solicita los datos de las categorías finalizadas por el usuario que está accediendo a la aplicación, se muestra en una tarjeta el nombre del campeonato, categoría, campeón y subcampeón ("Campeonato de Castilla-La Mancha""Senior Masculino <80kg""Iker (Nisseishi)""David (Kidokan)").
Estos datos se borrarán cada miércoles posterior al fin de semana de competición para evitar saturación e incentivar el poder y la comunicación del administrador.

#### 2.3 Marcador Kumite

Esta función muestra por pantalla un marcador de la modalidad de kumite que cumple con los requisitos de la WKF (World Karate Federation), se podrán seleccionar los minutos del combate entre 3:00, 2:00 y 1:30, cuenta con puntuación, acumulación de amonestaciones (Chui) hasta 5 cumpliendo con la nueva normativa, y también cuenta con la gestión del primer punto anotado (Senshu) para determinar un vencedor en caso de empate y marcador empatado distinto de 0-0, se podrá proyectar a otros dispositivos como televisiones para su uso en marcadores de competiciones de bajo nombre que no requieran de una base de datos conectada para la gestión del campeonato.

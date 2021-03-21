# Projet_iiot_2021_AuFeuLesPompiers

**Functional Context**
Fire prevention application for firefighters. 
This application allows the use of sensors positioned on sensitive points to prevent the onset of fires.
It sends an alert when the temperature threshold is reached. The firefighter can go on site to monitor the area. He can access the history of sensor measurements. The firefighter can also authenticate to the application using an nfc badge in order to re-perform a measurement at the alert location to ensure that the temperature has returned to normal.

**Developpers**  
_Alfred Gaillard_ (contact@alfred-gaillard.fr), _Alexandre Brunet_ (alexandre.brunet@etudiant.univ-lr.fr) 

**Standards** 
- Languages used : English for everything
- Naming Convention : https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html && https://source.android.com/setup/contribute/code-style#follow-field-naming-conventions
- Git : GitFlow type ; Merge request for **aBranch** -> **master**
- IoT environment : This application communicates with iBeacons temperature sensors.
- Technical : firebase database, used to store users and alert histories
- Devices : Android smartphones
- Api used : Api 23 to get more than 80% of devices

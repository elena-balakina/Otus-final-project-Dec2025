# Football Statistics

Spring Boot web application that tracks teams, players, and match results using Object-Oriented Programming (OOP) 
principles, a relational database, Kafka and CI/CD - GitHub Actions

## Project Description

Application that stores the data of football match statistics

✅ Layered architecture (Controller, Service, Repository, Model layers).<br>
✅ REST endpoints for all CRUD operations.<br>
✅ Validation and error handling.<br>
✅ Unit tests for each controller class.<br>
✅ Producing Kafka messages for different statistics.<br>


Classes:
---------
🏃 Player
🧑‍🏫 Coach
👨‍👨 Team
⚽ Match
⚽ Goal
🏁  Championship 

🛢️ PostgreSQL as a database

API:
----
Swagger: http://localhost:9090/swagger-ui/index.html
-------
POST   /api/teams                  
GET    /api/teams                  
GET    /api/teams/{id}
PUT    /api/teams/{id}             
DELETE /api/teams/{id}

POST   /api/players                
GET    /api/players                
GET    /api/players/{id}
PATCH  /api/players/{id}           
DELETE /api/players/{id}

POST   /api/coaches                
GET    /api/coaches
GET    /api/coaches/{id}
PATCH  /api/coaches/{id}
DELETE /api/coaches/{id}

POST   /api/matches                
GET    /api/matches
GET    /api/matches/{id}
PATCH  /api/matches/{id}              
POST   /api/matches/{id}/result  
DELETE /api/matches/{id}

GET    /api/stats/teams/{id}       ?year=
GET    /api/stats/players/{id}     ?year=
GET    /api/stats/top-teams        ?year=&limit=
GET    /api/stats/top-scorers      ?teamId=&year=&limit=

POST   /api/championships  
GET    /api/championships        
GET    /api/championships/{id}
PATCH  /api/championships/{id}
DELETE /api/championships/{id}

POST   /api/goals               
GET    /api/goals
GET    /api/goals/{id}
GET    /api/goals/player/{player_id}
PATCH  /api/goals/{id}               
DELETE /api/goals/{id}

----
Kafka
-----
- statistics.teams.out
- statistics.top-teams.out
- statistics.players.out
- statistics.top-scores.out

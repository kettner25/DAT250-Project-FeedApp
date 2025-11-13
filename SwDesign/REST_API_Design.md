# Rest API design



| Base               | Name       | Route   | Method | Body | Response           | Auth          | Desc                                                                        |
|--------------------|------------|---------|--------|------|--------------------|---------------|-----------------------------------------------------------------------------|
| /users             |            |         |        |      |                    |               |                                                                             |
|                    | getAll     | /       | GET    | -    | List\<user>        | ADMIN         |                                                                             |
|                    | getUser    | /{uid}  | GET    | -    | user               | ADMIN         |                                                                             |
|                    | editUser   | /{uid}  | PUT    | user | user               | ADMIN         | Only updates the app DB                                                     |
|                    | deleteUser | /{uid}  | DELETE | -    | boolean            | ADMIN         | Only updates the app DB                                                     |
| /me                |            |         |        |      |                    |               |                                                                             |
|                    | getMe      | /       | GET    | -    | user               | USER          |                                                                             |
|                    | getMyPolls | /polls/ | GET    | -    | List\<poll>        | USER          |                                                                             |
|                    | getMyVotes | /votes/ | GET    | -    | List\<votes>       | USER          |                                                                             |
| /polls             |            |         |        |      |                    |               |                                                                             |
|                    | getAll     | /       | GET    | -    | List\<poll>        | public        |                                                                             |
|                    | getPoll    | /{pid}  | GET    | -    | poll               | public        |                                                                             |
|                    | createPoll | /       | POST   | poll | poll               | USER \| ADMIN |                                                                             |
|                    | editPoll   | /{pid}  | PUT    | poll | poll               | USER \| ADMIN | Only adding options or reorder                                              |
|                    | deletePoll | /{pid}  | DELETE | -    | boolean            | USER \| ADMIN |                                                                             |
| /polls/{pid}/votes |            |         |        |      |                    |               |                                                                             |
|                    | vote       | /       | POST   | vote | vote id (int)      | public        |                                                                             |
|                    | unvote     | /{vid}  | DELETE | -    | boolean            | public        |                                                                             |
| /polls/{pid}/count |            |         |        |      |                    |               |                                                                             |
|                    | getCount   | /       | GET    | -    | List\<OptionCount> | public        | Returns collection of options and vote counts for each {option, vote_count} |

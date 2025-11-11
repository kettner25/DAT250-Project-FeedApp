# Rest API design



| Base               | Name         | Route   | Method | Body | Response           | Auth                     | Desc                                                                        |
|--------------------|--------------|---------|--------|------|--------------------|--------------------------|-----------------------------------------------------------------------------|
| /users             |              |         |        |      |                    |                          |                                                                             |
|                    | getAll       | /       | Get    | -    | List\<user>        | Admin                    |                                                                             |
|                    | getUser      | /{uid}  | Get    | -    | user               | Admin \| user.id = uid   |                                                                             |
|                    | create       | /       | Post   | user | user               | -                        |                                                                             |
|                    | edit         | /{uid}  | Put    | user | user               | Admin \| user.id = uid   |                                                                             |
|                    | delete       | /{uid}  | Delete | -    | boolean            | Admin \| user.id = uid   |                                                                             |
|                    | login (view) | /login  | Get    | -    |                    | -                        | => KeyCloak                                                                 |
|                    | login        | /login  | Post   |      |                    | -                        | => KeyCloak                                                                 |
|                    | logout       | /logout | Get    |      |                    | User \| Admin            | => KeyCloak                                                                 |
| /users/{uid}/polls |              |         |        |      |                    |                          |                                                                             |
|                    | getAll       | /       | Get    | -    | List\<poll>        | Admin \| user.id = uid   |                                                                             |
|                    | getPoll      | /{pid}  | Get    | -    | poll               | Admin \| user.id = uid   |                                                                             |
|                    | create       | /       | Post   | poll | poll               | Admin \| user.id = uid   |                                                                             |
|                    | edit         | /{pid}  | Put    | poll | poll               | Admin \| user.id = uid   | Only adding options or reorder                                              |
|                    | delete       | /{pid}  | Delete | -    | boolean            | Admin \| user.id = uid   |                                                                             |
| /polls             |              |         |        |      |                    |                          |                                                                             |
|                    | getAll       | /       | Get    | -    | List\<poll>        | -                        |                                                                             |
|                    | getPoll      | /{pid}  | Get    | -    | poll               | Admin                    |                                                                             |
|                    | create       | /       | Post   | poll | poll               | Admin                    |                                                                             |
|                    | edit         | /{pid}  | Put    | poll | poll               | Admin                    | Only adding options or reorder                                              |
|                    | delete       | /{pid}  | Delete | -    | boolean            | Admin                    |                                                                             |
| /polls/{pid}/votes |              |         |        |      |                    |                          |                                                                             |
|                    | vote         | /       | Post   | vote | vote id (int)      | -                        |                                                                             |
|                    | unvote       | /{vid}  | Delete | -    | boolean            | User that voted \| Admin |                                                                             |
| /polls/{pid}/count |              |         |        |      |                    |                          |                                                                             |
|                    | GetCount     | /       | Get    | -    | List\<OptionCount> | -                        | Returns collection of options and vote counts for each {option, vote_count} |

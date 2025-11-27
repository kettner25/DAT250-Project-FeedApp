# Details

Date : 2025-11-27 13:16:00

Directory c:\\Users\\Matthias\\Documents\\GitHub\\DAT250-Project-FeedApp

Total : 54 files,  3513 codes, 168 comments, 572 blanks, all 4253 lines

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [.github/workflows/build-and-deploy.yml](/.github/workflows/build-and-deploy.yml) | YAML | 58 | 0 | 14 | 72 |
| [.github/workflows/test.yml](/.github/workflows/test.yml) | YAML | 31 | 0 | 7 | 38 |
| [Dockerfile](/Dockerfile) | Docker | 22 | 0 | 9 | 31 |
| [README.md](/README.md) | Markdown | 35 | 0 | 7 | 42 |
| [SwDesign/Database\_scheme.md](/SwDesign/Database_scheme.md) | Markdown | 28 | 0 | 4 | 32 |
| [SwDesign/REST\_API\_Design.md](/SwDesign/REST_API_Design.md) | Markdown | 23 | 0 | 4 | 27 |
| [backend/src/main/java/no/hvl/group17/feedapp/FeedAppApplication.java](/backend/src/main/java/no/hvl/group17/feedapp/FeedAppApplication.java) | Java | 10 | 0 | 5 | 15 |
| [backend/src/main/java/no/hvl/group17/feedapp/conf/CorsConfig.java](/backend/src/main/java/no/hvl/group17/feedapp/conf/CorsConfig.java) | Java | 24 | 0 | 9 | 33 |
| [backend/src/main/java/no/hvl/group17/feedapp/conf/RabbitMQConfig.java](/backend/src/main/java/no/hvl/group17/feedapp/conf/RabbitMQConfig.java) | Java | 15 | 0 | 5 | 20 |
| [backend/src/main/java/no/hvl/group17/feedapp/conf/RedisConfig.java](/backend/src/main/java/no/hvl/group17/feedapp/conf/RedisConfig.java) | Java | 25 | 0 | 8 | 33 |
| [backend/src/main/java/no/hvl/group17/feedapp/conf/SecurityConfig.java](/backend/src/main/java/no/hvl/group17/feedapp/conf/SecurityConfig.java) | Java | 23 | 0 | 7 | 30 |
| [backend/src/main/java/no/hvl/group17/feedapp/controllers/AuthController.java](/backend/src/main/java/no/hvl/group17/feedapp/controllers/AuthController.java) | Java | 57 | 0 | 16 | 73 |
| [backend/src/main/java/no/hvl/group17/feedapp/controllers/MeController.java](/backend/src/main/java/no/hvl/group17/feedapp/controllers/MeController.java) | Java | 34 | 0 | 8 | 42 |
| [backend/src/main/java/no/hvl/group17/feedapp/controllers/PollController.java](/backend/src/main/java/no/hvl/group17/feedapp/controllers/PollController.java) | Java | 74 | 7 | 19 | 100 |
| [backend/src/main/java/no/hvl/group17/feedapp/controllers/PollVotesController.java](/backend/src/main/java/no/hvl/group17/feedapp/controllers/PollVotesController.java) | Java | 41 | 2 | 10 | 53 |
| [backend/src/main/java/no/hvl/group17/feedapp/controllers/UserController.java](/backend/src/main/java/no/hvl/group17/feedapp/controllers/UserController.java) | Java | 34 | 4 | 10 | 48 |
| [backend/src/main/java/no/hvl/group17/feedapp/domain/Option.java](/backend/src/main/java/no/hvl/group17/feedapp/domain/Option.java) | Java | 37 | 0 | 11 | 48 |
| [backend/src/main/java/no/hvl/group17/feedapp/domain/Poll.java](/backend/src/main/java/no/hvl/group17/feedapp/domain/Poll.java) | Java | 42 | 0 | 12 | 54 |
| [backend/src/main/java/no/hvl/group17/feedapp/domain/User.java](/backend/src/main/java/no/hvl/group17/feedapp/domain/User.java) | Java | 38 | 0 | 11 | 49 |
| [backend/src/main/java/no/hvl/group17/feedapp/domain/Vote.java](/backend/src/main/java/no/hvl/group17/feedapp/domain/Vote.java) | Java | 36 | 0 | 10 | 46 |
| [backend/src/main/java/no/hvl/group17/feedapp/models/OptionCount.java](/backend/src/main/java/no/hvl/group17/feedapp/models/OptionCount.java) | Java | 10 | 0 | 4 | 14 |
| [backend/src/main/java/no/hvl/group17/feedapp/repositories/PollRepo.java](/backend/src/main/java/no/hvl/group17/feedapp/repositories/PollRepo.java) | Java | 11 | 0 | 5 | 16 |
| [backend/src/main/java/no/hvl/group17/feedapp/repositories/UserRepo.java](/backend/src/main/java/no/hvl/group17/feedapp/repositories/UserRepo.java) | Java | 14 | 0 | 7 | 21 |
| [backend/src/main/java/no/hvl/group17/feedapp/repositories/VoteRepo.java](/backend/src/main/java/no/hvl/group17/feedapp/repositories/VoteRepo.java) | Java | 14 | 0 | 7 | 21 |
| [backend/src/main/java/no/hvl/group17/feedapp/security/JwtAuthFilter.java](/backend/src/main/java/no/hvl/group17/feedapp/security/JwtAuthFilter.java) | Java | 47 | 0 | 17 | 64 |
| [backend/src/main/java/no/hvl/group17/feedapp/security/JwtUtil.java](/backend/src/main/java/no/hvl/group17/feedapp/security/JwtUtil.java) | Java | 39 | 0 | 12 | 51 |
| [backend/src/main/java/no/hvl/group17/feedapp/services/PollService.java](/backend/src/main/java/no/hvl/group17/feedapp/services/PollService.java) | Java | 67 | 45 | 31 | 143 |
| [backend/src/main/java/no/hvl/group17/feedapp/services/UserService.java](/backend/src/main/java/no/hvl/group17/feedapp/services/UserService.java) | Java | 62 | 0 | 22 | 84 |
| [backend/src/main/java/no/hvl/group17/feedapp/services/VoteService.java](/backend/src/main/java/no/hvl/group17/feedapp/services/VoteService.java) | Java | 81 | 28 | 29 | 138 |
| [backend/src/main/resources/application.properties](/backend/src/main/resources/application.properties) | Java Properties | 26 | 6 | 8 | 40 |
| [backend/src/test/resources/application-test.properties](/backend/src/test/resources/application-test.properties) | Java Properties | 11 | 0 | 5 | 16 |
| [docker-compose.yml](/docker-compose.yml) | YAML | 59 | 14 | 7 | 80 |
| [frontend/index.html](/frontend/index.html) | HTML | 13 | 0 | 1 | 14 |
| [frontend/jsconfig.json](/frontend/jsconfig.json) | JSON with Comments | 16 | 0 | 1 | 17 |
| [frontend/package-lock.json](/frontend/package-lock.json) | JSON | 1,347 | 0 | 1 | 1,348 |
| [frontend/package.json](/frontend/package.json) | JSON | 19 | 0 | 1 | 20 |
| [frontend/public/favicon.svg](/frontend/public/favicon.svg) | XML | 1 | 4 | 0 | 5 |
| [frontend/public/silent-check-sso.html](/frontend/public/silent-check-sso.html) | HTML | 6 | 0 | 1 | 7 |
| [frontend/src/App.svelte](/frontend/src/App.svelte) | Svelte | 75 | 0 | 23 | 98 |
| [frontend/src/app.css](/frontend/src/app.css) | PostCSS | 25 | 0 | 5 | 30 |
| [frontend/src/components/Header.svelte](/frontend/src/components/Header.svelte) | Svelte | 22 | 0 | 2 | 24 |
| [frontend/src/components/OptionRow.svelte](/frontend/src/components/OptionRow.svelte) | Svelte | 87 | 0 | 16 | 103 |
| [frontend/src/components/PollBox.svelte](/frontend/src/components/PollBox.svelte) | Svelte | 154 | 0 | 32 | 186 |
| [frontend/src/components/PollEditorView.svelte](/frontend/src/components/PollEditorView.svelte) | Svelte | 188 | 0 | 36 | 224 |
| [frontend/src/components/PollListView.svelte](/frontend/src/components/PollListView.svelte) | Svelte | 43 | 0 | 8 | 51 |
| [frontend/src/components/Sidebar.svelte](/frontend/src/components/Sidebar.svelte) | Svelte | 49 | 0 | 8 | 57 |
| [frontend/src/lib/api.js](/frontend/src/lib/api.js) | JavaScript | 33 | 1 | 10 | 44 |
| [frontend/src/lib/auth.js](/frontend/src/lib/auth.js) | JavaScript | 117 | 14 | 27 | 158 |
| [frontend/src/lib/store.js](/frontend/src/lib/store.js) | JavaScript | 122 | 7 | 31 | 160 |
| [frontend/src/main.js](/frontend/src/main.js) | JavaScript | 11 | 0 | 2 | 13 |
| [frontend/svelte.config.js](/frontend/svelte.config.js) | JavaScript | 4 | 3 | 2 | 9 |
| [frontend/vite.config.js](/frontend/vite.config.js) | JavaScript | 5 | 1 | 2 | 8 |
| [gradle/wrapper/gradle-wrapper.properties](/gradle/wrapper/gradle-wrapper.properties) | Java Properties | 7 | 0 | 1 | 8 |
| [gradlew.bat](/gradlew.bat) | Batch | 41 | 32 | 22 | 95 |

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)
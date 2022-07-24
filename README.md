# protRenderFarm
App have two modules: server, client(instead of client can use telnet)
Прототип рендер фермы (без рендера). Клиент-серверное приложение. Регистрирует, логинит юзера через бд. Регистрирует задачу, обновляет статус выполнения (рандомное время выполнения), показывает текущие задачи и историю изменения статуса задач.

###Helper:
Usage: </br>
-h, **--help**           Show help message </br>
-l, **--login**          Log in **(-l user password)** </br>
-r, **--reg**            Registration **(-r user password)** </br>
-n, **--newtask**        Add new task </br>
-t, **--tasks**          Get all task </br>
-c, **--currenttasks**   Get tasks where status is RENDERING </br>
-s, **--statushistory**  Get history change tasks </br>

# Проверка порта на NeoForge 26.1.2.122 / Java 25

Начальный build падал на compileTestJava: оставшиеся ResourceLocation в mustard-тесте и отсутствие LDT compileOnly/localRuntime в test classpath. Исправлено использование Identifier и наследование тестовых конфигураций. Производственные механики не менялись.

Повторный build PASS. JUnit XML: 32 tests, 0 failures, 0 errors, 0 skipped. Проверены существующие сценарии модулей/культур/климата/стада/урона. Isolated packaged dedicated server с MineColonies 93f90d58ad: Done=True, exit=0. Логи: /root/home/agent-work/ports-audit-20261027/greenhouse-build{,-fixed}.log и greenhouse/qa-artifacts/server-smoke.log.

Предупреждение Netty kqueue на Linux в логах не устранено. Client/gameplay, рост культур, длительная работа персонала и сохранение/загрузка колонии этими тестами не подтверждены.

Воспроизведение: source /root/home/minecraft-dev-env.sh; bash ./gradlew --no-daemon --console=plain --max-workers=2 -Dorg.gradle.jvmargs=-Xmx1536m build -x neoFormJoined26.1.2-1DownloadAssets

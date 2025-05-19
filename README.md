# IoT-plarform

IoT-platform - это web сервер, для менеджмента ваших IoT устройств.

В нашем продукте есть:
1) Адаптер для устройств
2) rule-engine слой
3) слой нотификации (используется телеграм бот) https://github.com/SVA-work/IoTPlatformNotification

Запросы услуществляются по http и mqtt.

Также вся телеметрия передается на этот микросервис https://github.com/SVA-work/IoTTelemetrySaver, для сохранения
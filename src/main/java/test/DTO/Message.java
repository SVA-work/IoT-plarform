package test.DTO;

import test.entity.Device;

import java.util.Map;

// в DTO не должно быть реализации функций, это просто объект несущий инфу
// перенести эти функции в папку service (UserInfoService)
public class Message {

    private String login;
    private String password;

    public Message() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String successfulRegistration() {
        return "Вы успешно зарегистрировались.\n" +
                "Ваш логин: " + login + "\n" +
                "Ваш пароль: " + password;
    }

    public String entry() {
        return "Здравствуйте! Вы уже зарегистрированы.\n" +
                "Пожалуйста, отправьте ваш логин и пароль в следующем запросе, чтобы войти в систему.";
    }

    // через config порт
    public String successfulEntry() {
        return "Вы успешно вошли в систему.\n" +
        "Список доступных команд:\n" +
        "1)Добавить устройство.\n" +
        "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
        "http://localhost:8090/test/post/addDevice\n" +
        "2)Удалить устройство.\n" +
        "Для доступа к команде нужно отправить POST запрос на сервер по адресу: \n" +
        "http://localhost:8090/test/post/deleteDevice\n" +
        "3)Посмотреть список устройств.\n" +
        "http://localhost:8090/test/get/deviceInformation";
    }

    public boolean userVerification(String password, String login) {
        if (this.login == login && this.password == password) {
            return true;
        } else {
            return false;
        }
    }

    public String failedEntry() {
        return "Неверный пароль или логин.\n";
    }

    // получать порт через cofig
    public String actionsWithoutLogin() {
        return "Вы не вошли в систему.\n" +
        "Это можно сделать по адресу: \n" +
        "http://localhost:8090/test/post/entry";
    }

    //лучше переименовать
    public String deviceInformation(Map<String, Device> userDevices) {
        if (userDevices.size() == 0) {
            return "У вас больше нет устройств.";
        }
        StringBuilder info = new StringBuilder();
        for (Map.Entry<String, Device> entry : userDevices.entrySet()) {
            info.append(entry.getValue().getId()).append("\n");
        }
        return info.toString();
    }

    public String addDevice(Map<String, Device> userDevices) {
        StringBuilder info = new StringBuilder("Устройство успешно добавлено.\n");
        info.append("Список ваших устройств:\n");
        for (Map.Entry<String, Device> entry : userDevices.entrySet()) {
            info.append(entry.getValue().getId()).append("\n");
        }
        return info.toString();
    }

    public String deleteDevice(Map<String, Device> userDevices) {
        if (userDevices.size() == 0) {
            return "У вас больше нет устройств.";
        }
        StringBuilder info = new StringBuilder("Устройство успешно удалено.\n");
        info.append("Список ваших устройств:\n");
        for (Map.Entry<String, Device> entry : userDevices.entrySet()) {
            info.append(entry.getValue().getId()).append("\n");
        }
        return info.toString();
    }
    // в трех функциях одиннаковый код
}

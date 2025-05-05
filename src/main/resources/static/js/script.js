function closeModal() {
    document.getElementById('modal').style.display = 'none';
}

function closeRuleModal() {
    document.getElementById('rule-modal').style.display = 'none';
}

function getInputValue(id) {
    return document.getElementById(id).value.trim();
}

function setInputValue(id, value, disabled = false) {
    const el = document.getElementById(id);
    el.value = value;
    el.disabled = disabled;
}

function openModal(device = null, editable = false) {
    const modal = document.getElementById('modal');
    modal.style.display = 'flex';

    if (device) {
        setInputValue('device-name', device.deviceName, !editable);
        setInputValue('device-uuid', device.uuid, !editable);
        setInputValue('device-type', device.type, !editable);

        document.querySelector('h2').innerText = '';
        document.querySelector('.confirm_addition').innerText = '';
    } else {
        ['device-name', 'device-uuid', 'device-type'].forEach(id => setInputValue(id, '', false));

        const confirmBtn = document.getElementById('device-confirm');
        confirmBtn.innerText = 'Добавить';
        confirmBtn.onclick = saveDevice;
    }
}

async function saveDevice() {
    const name = getInputValue('device-name');
    const uuid = getInputValue('device-uuid');
    const type = getInputValue('device-type');
    const login = localStorage.getItem('login');

    if (!login) return alert('Пользователь не авторизован');
    if (!name || !uuid || !type) return alert('Для добавления устройства заполните все поля.');

    const device = {login, uuid, type, deviceName: name};

    try {
        const response = await fetch('http://localhost:8091/device/add', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(device)
        });

        if (!response.ok) throw new Error('Ошибка добавления устройства: ' + response.status);

        const savedDevice = await response.json();
        console.log('Устройство добавлено в БД:', savedDevice);

        const list = document.getElementById('list');
        list.appendChild(createDeviceElement(device));
        closeModal();
    } catch (error) {
        console.error('Ошибка при сохранении:', error);
        alert('Ошибка при добавлении устройства: ' + error.message);
    }
}

function createDeviceElement(device) {
    const item = document.createElement('div');
    item.className = 'item-container';
    item.style.display = 'flex';

    const info = document.createElement('div');
    info.className = 'device';
    info.innerHTML = `<span>${device.deviceName}</span>`;

    const buttons = [
        {class: 'settings', title: 'Настройки', handler: () => openRuleModal(device)},
        {class: 'info', title: 'Инфо', handler: () => openModal(device, false)},
        {class: 'trash', title: 'Удалить', handler: () => deleteDevice(device, item)}
    ];

    item.appendChild(info);
    buttons.forEach(({class: cls, handler, title, text = ''}) => {
        const btn = document.createElement('button');
        btn.className = `icon-button ${cls}`;
        btn.title = title;
        btn.innerText = text;
        btn.onclick = handler;
        item.appendChild(btn);
    });

    return item;
}

async function deleteDevice(device, element) {
    const login = localStorage.getItem('login');
    if (!login) return alert('Пользователь не авторизован');

    const body = JSON.stringify({...device, login});

    try {
        const response = await fetch('http://localhost:8091/device/delete', {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json'},
            body
        });

        if (!response.ok) throw new Error('Ошибка при удалении устройства: ' + response.status);

        const result = await response.json();
        console.log('Устройство удалено:', result);

        element?.remove();
        closeModal();
    } catch (error) {
        console.error('Ошибка при удалении устройства:', error);
        alert('Не удалось удалить устройство.');
    }
}

function openRuleModal(device) {
    const modal = document.getElementById('rule-modal');
    modal.style.display = 'flex';

    const ruleInput = document.getElementById('rule');
    const valueInput = document.getElementById('rule-value');
    const operatorSelect = document.getElementById('operator');
    const confirmButton = document.getElementById('rule-confirm');

    ruleInput.value = '';
    valueInput.value = '';
    operatorSelect.value = '=';

    confirmButton.onclick = async () => {
        const rule = ruleInput.value.trim();
        const value = parseFloat(valueInput.value.trim());
        const comparison = operatorSelect.value;
        const login = localStorage.getItem('login');

        if (!rule || isNaN(value) || !comparison) return alert('Пожалуйста, заполните все поля корректно.');
        if (!login) return alert('Пользователь не авторизован');

        const request = {
            login,
            deviceName: device.deviceName,
            rule,
            value,
            comparison,
            updateRule: ""
        };

        try {
            const response = await fetch('http://localhost:8091/rule/apply', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(request)
            });

            if (!response.ok) throw new Error('Ошибка при применении правила: ' + response.status);

            const result = await response.json();
            console.log('Правило применено:', result);
            closeRuleModal();
        } catch (error) {
            console.error('Ошибка при добавлении правила:', error);
            alert('Ошибка при добавлении правила: ' + error.message);
        }
    };
}

async function preloadDevices() {
    const login = localStorage.getItem('login');

    try {
        const response = await fetch(`http://localhost:8091/user/${login}/devices`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Ошибка при загрузке устройств: ' + response.status);
        }

        const devices = await response.json();
        console.log('Загруженные устройства:', devices);

        const list = document.getElementById('list');
        if (!list) throw new Error('Элемент #list не найден');

        devices.forEach(device => {
            const element = createDeviceElement(device);
            list.appendChild(element);
        });

    } catch (error) {
        console.error('Ошибка при загрузке устройств:', error);
        alert('Не удалось загрузить устройства: ' + error.message);
    }
}
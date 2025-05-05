function closeModal() {
    document.getElementById('modal').style.display = 'none';
}

function closeRuleModal() {
    document.getElementById('rule-modal').style.display = 'none';
}

function openModal(device = null, editable = false) {
    const modal = document.getElementById('modal');
    modal.style.display = 'flex';

    const nameField = document.getElementById('device-name');
    const uuidField = document.getElementById('device-uuid');
    const typeField = document.getElementById('device-type');

    if (device) {
        nameField.value = device.deviceName.trim();
        uuidField.value = device.uuid.trim();
        typeField.value = device.type.trim();

        nameField.disabled = !editable;
        uuidField.disabled = !editable;
        typeField.disabled = !editable;

        document.querySelector('h2').innerText = null;
        document.querySelector('.confirm_addition').innerText = null;
    } else {
        nameField.value = '';
        uuidField.value = '';
        typeField.value = '';
        nameField.disabled = false;
        uuidField.disabled = false;
        typeField.disabled = false;
        document.querySelector('.confirm_addition').innerText = 'Добавить';
        document.querySelector('.confirm_addition').onclick = saveDevice;
    }
}

async function saveDevice() {
    const name = document.getElementById('device-name').value.trim();
    const uuid = document.getElementById('device-uuid').value.trim();
    const type = document.getElementById('device-type').value.trim();

    const login = localStorage.getItem('login');
    if (!login) {
        alert('Пользователь не авторизован');
        return;
    }

    if (!name || !uuid || !type) {
        alert('Для добавления устройства заполните все поля.');
        return;
    }

    const deviceRequest = {
        login: login,
        uuid: uuid,
        type: type,
        deviceName: name
    };

    try {
        const response = await fetch('http://localhost:8091/device/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(deviceRequest)
        });

        if (!response.ok) {
            throw new Error('Ошибка добавления устройства: ' + response.status);
        }

        const savedDevice = await response.json();
        console.log('Устройство добавлено в БД:', savedDevice);

        const list = document.getElementById('list');

        const itemContainer = document.createElement('div');
        itemContainer.className = 'item-container';
        itemContainer.style.display = 'flex';

        const deviceInfo = document.createElement('div');
        deviceInfo.className = 'device';
        deviceInfo.innerHTML = `<span>${name}</span>`;

        const settingsButton = document.createElement('button');
        settingsButton.className = 'icon-button settings';
        settingsButton.onclick = function () {
            openRuleModal(deviceRequest);
        };

        const infoButton = document.createElement('button');
        infoButton.className = 'icon-button info';
        infoButton.onclick = function () {
            openModal(deviceRequest, false);
        };
        const scheduleButton = document.createElement('button');
        scheduleButton.className = 'icon-button schedule';
        scheduleButton.onclick = function () {
            buildSchedule(deviceRequest);
        };
        const trashButton = document.createElement('button');
        trashButton.className = 'icon-button trash';
        trashButton.innerText = 'здесь может быть ваша мусорка';
        trashButton.onclick = function () {
            deleteDevice(deviceRequest, itemContainer);
        }

        itemContainer.appendChild(deviceInfo);
        itemContainer.appendChild(settingsButton);
        itemContainer.appendChild(infoButton);
        itemContainer.appendChild(scheduleButton);
        itemContainer.appendChild(trashButton);

        list.appendChild(itemContainer);
        closeModal();

    } catch (error) {
        console.error('Ошибка при сохранении:', error);
        alert('Ошибка при добавлении устройства: ' + error.message);
    }
}

async function deleteDevice(device, deviceElement) {
    const login = localStorage.getItem('login');
    if (!login) {
        alert('Пользователь не авторизован');
        return;
    }

    const requestData = {
        login: login,
        uuid: device.uuid,
        type: device.type,
        deviceName: device.deviceName
    };

    try {
        const response = await fetch('http://localhost:8091/device/delete', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            throw new Error('Ошибка при удалении устройства: ' + response.status);
        }

        const result = await response.json();
        console.log('Устройство удалено:', result);

        if (deviceElement && deviceElement.parentNode) {
            deviceElement.remove();
        }

        closeModal();

    } catch (error) {
        console.error('Ошибка при удалении устройства:', error);
        alert('Не удалось удалить устройство.');
    }
}


function openRuleModal(device) {
    const ruleModal = document.getElementById('rule-modal');
    ruleModal.style.display = 'flex';

    const ruleField = document.getElementById('rule-input');
    ruleField.value = document.getElementById('rule');

    document.querySelector('.confirm_rule_addition').onclick = function () {
        const rule = ruleField.value.trim();
        if (!rule) {
            alert('Введите правило!');
            return;
        }

        console.log(`Добавлено правило для устройства ${device.deviceName}: ${rule}`);

        closeRuleModal();
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
        if (!list) {
            throw new Error('Элемент #list не найден');
        }

        devices.forEach(device => {
            const itemContainer = document.createElement('div');
            itemContainer.className = 'item-container';
            itemContainer.style.display = 'flex';

            const deviceInfo = document.createElement('div');
            deviceInfo.className = 'device';
            deviceInfo.innerHTML = `<span>${device.deviceName}</span>`;

            const settingsButton = document.createElement('button');
            settingsButton.className = 'icon-button settings';
            settingsButton.onclick = function () {
                openRuleModal(device);
            };

            const infoButton = document.createElement('button');
            infoButton.className = 'icon-button info';
            infoButton.onclick = function () {
                openModal(device, false);
            };

            const trashButton = document.createElement('button');
            trashButton.className = 'icon-button trash';
            trashButton.innerText = 'здесь может быть ваша мусорка';
            trashButton.onclick = function () {
                deleteDevice(device, itemContainer);
            }

            itemContainer.appendChild(deviceInfo);
            itemContainer.appendChild(settingsButton);
            itemContainer.appendChild(infoButton);
            itemContainer.appendChild(trashButton);

            list.appendChild(itemContainer);
        });

    } catch (error) {
        console.error('Ошибка при загрузке устройств:', error);
        alert('Не удалось загрузить устройства: ' + error.message);
    }
}



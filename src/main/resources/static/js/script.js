function addItem() {
    openModal();
}

function closeModal() {
    document.getElementById("modal").style.display = "none";
}

function openModal(device = null, editable = false, deviceElement = null) {
    const modal = document.getElementById("modal");
    modal.style.display = "flex";

    const nameField = document.getElementById("device-name");
    const uuidField = document.getElementById("device-uuid");
    const typeField = document.getElementById("device-type");
    const ruleField = document.getElementById("device-rule");

    if (device) {
        nameField.value = device.name;
        uuidField.value = device.uuid;
        typeField.value = device.type;
        ruleField.value = device.rule;

        nameField.disabled = !editable;
        uuidField.disabled = !editable;
        typeField.disabled = !editable;
        ruleField.disabled = !editable;

        document.querySelector(".confirm_addition").innerText = editable ? "Сохранить изменения" : "Ок";
        document.querySelector(".confirm_addition").onclick = function() {
            if (editable) {
                saveChanges(device, deviceElement);
            } else {
                closeModal();
            }
        };
    } else {
        nameField.value = "";
        uuidField.value = "";
        typeField.value = "";
        ruleField.value = "";
        nameField.disabled = false;
        uuidField.disabled = false;
        typeField.disabled = false;
        ruleField.disabled = false;
        document.querySelector(".confirm_addition").innerText = "Добавить";
        document.querySelector(".confirm_addition").onclick = saveItem;
    }
}

function saveItem() {
    const name = document.getElementById("device-name").value.trim();
    const uuid = document.getElementById("device-uuid").value.trim();
    const type = document.getElementById("device-type").value.trim();
    const rule = document.getElementById("device-rule").value.trim();

    if (!name || !uuid || !type || !rule) {
        alert("Для добавления устройства заполните все поля.");
        return;
    }

    const device = { name, uuid, type, rule };

    const list = document.getElementById("list");

    const itemContainer = document.createElement("div");
    itemContainer.className = "item-container";
    itemContainer.style.display = "flex";

    const deviceInfo = document.createElement("div");
    deviceInfo.className = "device";
    deviceInfo.innerHTML = `<span>${name}</span>`;

    const settingsButton = document.createElement("button");
    settingsButton.className = "icon-button settings";
    settingsButton.onclick = function() {
        openModal(device, true, deviceInfo.querySelector("span"));
    };

    const infoButton = document.createElement("button");
    infoButton.className = "icon-button info";
    infoButton.onclick = function() {
        openModal(device, false);
    };

    itemContainer.appendChild(deviceInfo);
    itemContainer.appendChild(settingsButton);
    itemContainer.appendChild(infoButton);

    list.appendChild(itemContainer);

    closeModal();
}

function saveChanges(device, deviceNameElement) {
    device.name = document.getElementById("device-name").value.trim();
    device.uuid = document.getElementById("device-uuid").value.trim();
    device.type = document.getElementById("device-type").value.trim();
    device.rule = document.getElementById("device-rule").value.trim();

    if (deviceNameElement) {
        deviceNameElement.textContent = device.name;
    }

    closeModal();
}
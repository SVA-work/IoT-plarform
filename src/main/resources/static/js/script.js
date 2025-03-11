function addItem() {
    document.getElementById("modal").style.display = "flex";
}

function closeModal() {
    document.getElementById("modal").style.display = "none";
}

function saveItem() {
    let name = document.getElementById("device-name").value.trim();
    let uuid = document.getElementById("device-uuid").value.trim();
    let type = document.getElementById("device-type").value.trim();

    if (!name || !uuid || !type) {
        alert("Для добавления устройства заполните все поля.");
        return;
    }

    const list = document.getElementById("list");

    const itemContainer = document.createElement("div");
    itemContainer.className = "item-container";
    itemContainer.style.display = "flex";

    const deviceInfo = document.createElement("div");
    deviceInfo.className = "device";
    deviceInfo.innerHTML = `
        <span>${name}</span>
    `;

    const settingsButton = document.createElement("button");
    settingsButton.className = "icon-button settings";

    const infoButton = document.createElement("button");
    infoButton.className = "icon-button info";

    itemContainer.appendChild(deviceInfo);
    itemContainer.appendChild(settingsButton);
    itemContainer.appendChild(infoButton);

    list.appendChild(itemContainer);

    closeModal();

    document.getElementById("device-name").value = "";
    document.getElementById("device-uuid").value = "";
    document.getElementById("device-type").value = "";
}


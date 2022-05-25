let artistInputField = document.getElementById("artistInputField");
let artistList = document.getElementById("artistList");
let addSongForm = document.getElementById("addSongForm");
let addSongTitleField = document.getElementById("addSongTitleField");
let addSongSubmitBtn = document.getElementById("addSongSubmitBtn");
let addSongResetBtn = document.getElementById("addSongResetBtn");
let loadFileInput = document.getElementById("loadFileInput");
let titleAddDiv = document.getElementById("titleAddDiv");
let artistAddDiv = document.getElementById("artistAddDiv");

loadFileInput.onchange = async function () {
    let formData = new FormData();
    formData.append("file", loadFileInput.files[0]);

    let response = await fetch("/songs/fileInfo", {
        method: "POST",
        body: formData,
    });
    let tags;
    await response.json().then(res => { tags = res; });
    console.log(typeof tags);
    console.log(tags);

    resetSongTitleField();
    resetArtistList();

    setAddSongTitleField(tags.TITLE);
    addArtistToList(tags.ARTIST);

    titleAddDiv.style.display = "block";
    artistAddDiv.style.display = "block";
    addSongSubmitBtn.style.display = "inline";
    addSongResetBtn.style.display = "inline";

}



addSongResetBtn.onclick = function () {
    resetArtistList();
    titleAddDiv.style.display = "none";
    artistAddDiv.style.display = "none";
}

function setAddSongTitleField(title) {
    if (title === null || title === undefined) {
        console.error("Song title is not set.");
        return;
    }
    if (!/\S/.test(title)) {
        console.error("Song title is blank.");
        return;
    }
    addSongTitleField.value = title;
}

function resetSongTitleField() {
    addSongTitleField.value = "";
}

function resetArtistList() {
    artistList.innerHTML = "";
}

function addArtistFromInput(artistInput, artistList) {
    addArtistToList(artistInput.value, artistList);
    artistInput.value = "";
}

function addArtistToList(artist, artistList) {
    if (artist === null || artist === undefined) {
        console.error("Artist is not set.");
        return;
    }
    if (!/\S/.test(artist)) {
        console.error("Artist is blank.");
        return;
    }
    if (artistListContains(artistList, artist)){
        console.error("Duplicate artist.");
        return;
    }
    let br = document.createElement('br');

    let textField = document.createElement('input');
    textField.setAttribute("type", "text");
    textField.setAttribute("name", "artists");
    textField.value = artist;

    let deleteButton = document.createElement('button');
    deleteButton.classList.add("btn", "btn-outline-secondary");
    deleteButton.setAttribute("type", "button");
    deleteButton.onclick = function () {
        artistList.removeChild(textField);
        artistList.removeChild(deleteButton);
        artistList.removeChild(br);
        if(artistList.childElementCount === 0)
            artistList.style.display = "none";
    }
    deleteButton.value = "Delete";
    deleteButton.innerHTML = "Delete";

    artistList.appendChild(textField);
    artistList.appendChild(deleteButton);
    artistList.appendChild(br);
}

function artistListContains(artistList, artist) {
    for ( let li of artistList.getElementsByTagName("li")) {
        if (li.textContent.toLowerCase() === artist.toLowerCase())
            return true;
    }
    return false;
}
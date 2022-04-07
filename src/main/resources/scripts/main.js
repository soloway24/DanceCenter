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

// addSongSubmitBtn.onclick = function (e) {
//     let formData = new FormData(addSongForm);
// //check new form-data values
//     for (let p of formData) {
//         let title = p[0];
//         let value = p[1];
//         console.log(title, value)
//     }
//     e.preventDefault();
// }

function addArtistFromInputField() {
    addArtistToList(artistInputField.value);
    artistInputField.value = "";
}

function addArtistToList(artist) {
    if (artist === null || artist === undefined) {
        console.error("Artist is not set.");
        return;
    }
    if (!/\S/.test(artist)) {
        console.error("Artist is blank.");
        return;
    }
    if (artistListContains(artist)){
        console.error("Duplicate artist.");
        return;
    }

    let input = document.createElement('input');
    input.setAttribute("type", "hidden");
    input.setAttribute("name", "artists");
    input.value = artist;
    let li = document.createElement('li');
    li.classList.add('list-group-item');
    li.appendChild(document.createTextNode(artist));
    artistList.appendChild(li);
    artistList.appendChild(input);
}

function artistListContains(artist) {
    for ( let li of artistList.getElementsByTagName("li")) {
        if (li.textContent.toLowerCase() === artist.toLowerCase())
            return true;
    }
    return false;
}
let addSongTitleField = document.getElementById("addSongTitleField");

let loadFileInput = document.getElementById("loadFileInput");



let editPostDescriptionField = document.getElementById("editPostDescriptionField");
let editSongTitleInput = document.getElementById("editSongTitleInput");

let artistInputField = document.getElementById("artistInputField");
let editedArtistList = document.getElementById("editedArtistList");
let editSongForm = document.getElementById("editSongForm");

editSongForm.onsubmit = function (e) {
    let title = editSongTitleInput.value;
    let artElems = document.getElementsByName("artists");
    let artValues = artElems.map(elem => elem.value);
    if(!validateSong(title, artValues)){
        e.preventDefault();
    }
}

function validateSong(title, artists) {
    if (title === null || title === undefined) {
        alert("Song title is not set.");
        return false;
    }
    if (!/\S/.test(title)) {
        alert("Song title is blank.");
        return false;
    }
    for(let art of artists){
        if (!/\S/.test(art)) {
            alert("Some of artists are blank.");
            return false;
        }
    }
    return true;
}

$('#editSongModal').on('show.bs.modal', function (event) {
    resetEditedArtistList();
    let button = $(event.relatedTarget);
    let id = button.data('song_id');
    let title = button.data('song_title');
    let artists = button.data('song_artists');
    artists = artists.substring(1, artists.length - 1).split(',');
    console.log(id);
    console.log(title);
    console.log(artists);
    let modal = $(this);
    modal.find('#editSongTitleInput').val(title);
    modal.find('#editedSongIdInput').val(id);
    for(const art of artists)
        addArtistToList(art, editedArtistList);
})

function resetEditedArtistList() {
    editedArtistList.innerHTML = "";
}

loadFileInput.onchange = async function () {

    const nFiles = loadFileInput.files.length;
    if(nFiles < 1){
        alert("Choose at least 1 file");
        e.preventDefault();
        return;
    }

    let formData = new FormData();
    for(let i = 0; i < nFiles; i++){
        formData.append("files", loadFileInput.files[i]);
    }

    let getFileTagsResponse = await fetch("/songs/multipleFileInfo", {
        method: "POST",
        body: formData,
    });
    let tagList;
    await getFileTagsResponse.json().then(res => { tagList = res; });

    let titles = [];
    let artists = [];
    for(let i = 0; i < nFiles; i++){
        titles.push(tagList[i].TITLE);
        const songArtists = tagList[i].ARTIST.split('/');
        artists.push(songArtists);
    }
    formData.append("titles", JSON.stringify(titles));
    formData.append("artists", JSON.stringify(artists));

    let addSongsResponse = await fetch("/songs/multipleAdd", {
                method: "POST",
                body: formData
            });
    let addedIdsJson = await addSongsResponse.json();
    let addedIds = JSON.stringify(addedIdsJson);
    addedIds = addedIds.substring(1, addedIds.length - 1);

    resetLoadFileInput();

    if(window.location.href.substring(window.location.href.length - 7) === "addPost")
        window.location.href = "/addPost/" + addedIds;
    else
        window.location.href = window.location.href + ',' + addedIds;

}

addSongsResetBtn.onclick = function () {
    resetLoadFileInput();
    resetPostDescription();
}

function resetLoadFileInput() {loadFileInput.value = ""; }
function resetPostDescription() {editPostDescriptionField = ""; }


const editTitleInputPrefix = "editTitleInput";
const editTitleDivPrefix = "editTitleDiv";
const editArtistsDivPrefix = "editArtistsDiv";
const editArtistsInputDivPrefix = "editArtistsInputDiv";
const addArtistInputPrefix = "addArtistInput";
const addArtistButtonPrefix = "addArtistButton";
const artistListDivPrefix = "artistListDiv";


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


function addArtistFromInput(artistInput, artistList) {
    addArtistToList(artistInput.value, artistList);
    artistInput.value = "";
}


function addArtistToList(artist, artistList) {
    console.log("Adding artist: " + artist);
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
    deleteButton.classList.add("btn", "btn-danger");
    deleteButton.setAttribute("type", "button");
    deleteButton.onclick = function () {
        artistList.removeChild(textField);
        artistList.removeChild(deleteButton);
        artistList.removeChild(br);
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
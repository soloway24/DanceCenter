let loadFileInput = document.getElementById("loadFileInput");

let editPostDescriptionTextArea = document.getElementById("editPostDescriptionTextArea");
let editSongTitleInput = document.getElementById("editSongTitleInput");

let editSongArtistInput = document.getElementById("editSongArtistInput");
let editedArtistList = document.getElementById("editedArtistList");
let editSongForm = document.getElementById("editSongForm");

let addPostBtn = document.getElementById("addPostBtn");
let resetPostBtn = document.getElementById("resetPostBtn");

addPostBtn.onclick = async function () {
    let description = editPostDescriptionTextArea.value;
    let songIds = getParamFromUrl(window.location.href, "postSongsIds");
    if(songIds === null){
        alert("Post should contain at least 1 song.")
        return;
    }
    let formData = new FormData();
    console.log(songIds);
    console.log(JSON.stringify(songIds));
    formData.append("description", description);
    formData.append("songIds", JSON.stringify(songIds));

    let addPostResponse = await fetch("/posts/add", {
        method:"POST",
        body: formData
    });
    if(addPostResponse.ok)
        window.location.href = addPostResponse.url;
    else
        window.location.href = "/errors/addPost";
}

resetPostBtn.onclick = async function () {
    resetPostDescription();
    resetLoadFileInput();

    let idsStr = getParamFromUrl(window.location.href, "postSongsIds");
    let redirectUrl;
    if(idsStr != null) {
        let idsArray = getIdsFromString(idsStr);
        let deleteResponse = await fetch("/songs/multipleDelete",
            {
                method:"POST",
                body: JSON.stringify(idsArray)
            });
        redirectUrl = deleteResponse.url;
    } else
        redirectUrl = "/posts/add";
    window.location.href = redirectUrl;
}

function getIdsFromString(strIds) {
    let idArrayStr = strIds.split(',');
    return idArrayStr.map(s => +s);
}

function getParamFromUrl(urlString, paramName) {
    let url = new URL(urlString);
    let resStr = url.searchParams.get(paramName);

    return resStr;
}

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
    if(artists.length === 0)
        artists = [];
    else
        artists = artists.substring(1, artists.length - 1).split(',');
    console.log(id);
    console.log(title);
    console.log(artists);
    console.log(typeof artists);
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

    let formData = new FormData();
    for(let i = 0; i < nFiles; i++){
        formData.append("files", loadFileInput.files[i]);
    }

    let tagList;
    let fileInfoSuccess = true;

    await fetch("/songs/multipleFileInfo", {
        method: "POST",
        body: formData,
    })
        .then(res => res.json())
        .then(data => tagList = data)
        .catch(() => {
            window.location.href = "/errors/fileInfo";
            fileInfoSuccess = false;
        });

    if(fileInfoSuccess) {
        let titles = [];
        let artists = [];
        for(let i = 0; i < nFiles; i++){
            titles.push(tagList[i].TITLE);
            const songArtists = tagList[i].ARTIST.split('/');
            artists.push(songArtists);
        }
        formData.append("titles", JSON.stringify(titles));
        formData.append("artists", JSON.stringify(artists));
        let addedIdsJson;
        let success = true;
        await fetch("/songs/multipleAdd", {
            method: "POST",
            body: formData
        })
            .then(res => res.json())
            .then(data => addedIdsJson = data)
            .catch(() => {
                window.location.href = "/errors/addSong";
                success = false;
            });
        console.log(addedIdsJson);
        resetLoadFileInput();
        if(success) {
            let addedIds = JSON.stringify(addedIdsJson);
            addedIds = addedIds.substring(1, addedIds.length - 1);

            let desc = editPostDescriptionTextArea.value;
            let postSongsIdsStr = getParamFromUrl(window.location.href, "postSongsIds");
            if(postSongsIdsStr != null) {
                let newIds = postSongsIdsStr + ',' + addedIds;
                if(!/\S/.test(desc)) //desc is blank
                    window.location.href = "/posts/add?postSongsIds=" + newIds;
                else
                    window.location.href = "/posts/add?postSongsIds=" + newIds + "&description=" + desc;
            } else {
                if(!/\S/.test(desc)) //desc is blank
                    window.location.href = "/posts/add?postSongsIds=" + addedIds;
                else
                    window.location.href = "/posts/add?postSongsIds=" + addedIds + "&description=" + desc;
            }
        }
    }
}

function resetLoadFileInput() {loadFileInput.value = ""; }
function resetPostDescription() {editPostDescriptionTextArea = ""; }

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
function getUsername() {
    try {
        let usernameIndex = document.cookie.indexOf('username=') + 9;
        if (usernameIndex == -1 + 9) {
            return undefined
        }
        return document.cookie.substring(usernameIndex)
    } catch (e) {
        return undefined;
    }
}

let sidebarItems = [
    ['Login', 'login.html'],
    ['Logout', 'login.html\" onclick="logout()'],
    ['Register', 'register.html'],
    ['Search Postings', 'search.html'],
    ['Manage Account', 'account.html'],
    ['Create Listing', 'createlisting.html'],
    ['My Bookings', 'bookings.html'],
    ['My Listings', 'listings.html'],
    ['My Ratings', 'ratings.html?id=' + getUsername()],
    ['Reports', 'reports.html']
]

let sidebar = document.getElementById('sidebar')

let username = getUsername();

if (username != undefined) {
    sidebar.innerHTML += '<h3> Hello ' + username + '</h3>'
}

for (let i in sidebarItems) {
    sidebar.innerHTML += '<a class="sidebar-item" href="' + sidebarItems[i][1] + '" >' + sidebarItems[i][0] + '</a>'
}

function logout() {
    console.log(document.cookie)
    document.cookie = 'username=; expires=Thur, 01 Jan 1970 12:00:00 UTC'
}

function getVal(id) {
    return document.getElementById(id).value
}
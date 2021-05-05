import React from 'react'
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import Typography from '@material-ui/core/Typography';
import { TextField } from '@material-ui/core';
import Snackbar from '@material-ui/core/Snackbar';
// import MuiAlert from '@material-ui/lab/Alert';
import axios from 'axios';
import firebase from 'firebase'
import { Alert } from '@material-ui/lab';

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyBOHwXYFskyv3Ck_4y1q-0Xj07eaCoOynA",
    authDomain: "spotify-server-4e6f2.firebaseapp.com",
    databaseURL: "https://spotify-server-4e6f2-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "spotify-server-4e6f2",
    storageBucket: "spotify-server-4e6f2.appspot.com",
    messagingSenderId: "144439452035",
    appId: "1:144439452035:web:3072f27a06c33c05a856b6"
};
// Initialize Firebase
const firebaseApp = firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging(firebaseApp);

const useStyles = makeStyles({
    card: {
        height: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
        width: '100%'
    },
    title: {
        fontSize: 14,
    },
    pos: {
        marginBottom: 12,
    },
});

export default function ChatBox() {
    const [connected, setConnected] = React.useState(false);
    const [text, setText] = React.useState('');
    const [chatID, setChatID] = React.useState('');
    const [ws, setWs] = React.useState();
    const classes = useStyles();
    const [registrationToken, setRegisterationToken] = React.useState('');
    const [init, setInit] = React.useState(true);
    const token = 'eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl9lbWFpbCI6Im1hbmdhQGhvdG1haWwuY29tIiwidG9rZW5fdXNlcl9pZCI6IjMiLCJ0b2tlbl9uYW1lIjoiTWFuZ29uZWVzZSIsInRva2VuX2JpdF9yYXRlIjoiMTI4IiwiZXhwaXJhdGlvbiI6MTYyMDI3NDkwMDE2MSwidG9rZW5fdXNlcm5hbWUiOiJtYW5nYSIsInRva2VuX3R5cGUiOiJ1c2VyIiwidG9rZW5faXNfcHJlbWl1bSI6ImYifQ.1wr2KYDcBZn1L1C32Gv3JBKrfbqKG9di2nWxHX0skaY'
    const [messages, setMessages] = React.useState([]);
    const [open, setOpen] = React.useState(false);
    const [notif, setNotif] = React.useState({});

    messaging.onMessage(payload => {
        // console.log('ayo fam xdd');
        console.log(`Payload: `, payload);
        setOpen(true);
        setNotif(payload.data);
    })

    const handleChange = (e) => {
        setText(e.target.value);
    }

    const handleSend = () => {
        if (connected) {
            ws.send(text);
        }
    }

    const handleCreate = async () => {
        if (!connected) {
            await axios.post(
                'http://127.0.0.1:8080/testing-rabbit/create-chat',
                {
                    "queue": "testing-rabbit",
                    "method": "create-chat",
                    "participants": "1;5;20"
                },
                {
                    headers: {
                        token: token
                    }
                }
            )
                .then(response => {
                    // console.log(`ChatID: ${response.data.data}`);
                    setWs(new WebSocket(`ws://127.0.0.1:9090/chat?chatID=${response.data.data}?token=${token}?FCM=${registrationToken}`));
                    setConnected(true);
                })
                .catch(err => {
                    console.log(err);
                })
        }
    }

    const handleConnect = async () => {
        setWs(new WebSocket(`ws://127.0.0.1:9090/chat?chatID=${text}?token=${token}?FCM=${registrationToken}`));
        setConnected(true);
        const data = {
            chatID: text
        }
        // console.log('wtf')
        await axios.post(
            'http://127.0.0.1:8080/testing-rabbit/get-chat-logs',
            data,
            {
                headers: {
                    token: token
                }
            }
        )
            .then(res => {
                // console.log('a7a'); 
                // console.log('mangoneese');
                // setMessages
                // console.log(JSON.parse(res.data.data));
                // console.log(Date.parse(JSON.parse(res.data.data)[0]["sentDate"]));
                let logs = JSON.parse(res.data.data)
                // console.log(logs);
                logs.map((log) => {
                    log["timeStamp"] = Date.parse(log["sentDate"]);
                })
                console.log(logs);
                setMessages(logs);

            })
            .catch(err => {
                console.log(err)
            })
    }

    const messagesRef = React.useRef();

    const handleInit = async () => {

        await axios.post(
            'http://127.0.0.1:8080/testing-rabbit/set-registration-token/',
            {
                "registrationToken": registrationToken
            },
            {
                headers: {
                    token: token
                }
            }
        ).then(res => {
            setInit(false);
            console.log(res);
        })
            .catch(err => console.log(err))
    }

    const handleClose = () => {
        setOpen(false);
    }

    React.useEffect(() => {
        // const initialize = async () => {
        //     messaging.getToken({
        //         vapidKey: 'BInx6M6lbCz-xhJu-HUzOiwECWTobyh85Xc7B1W3E9EYxRNVHkT4i6juYVDrVXrpK2fWNrHsiPf0DtVfH0zryr8'
        //     })
        //         .then((FCMToken) => {
        //             // console.log('aboos eedak yasta');
        //             setRegisterationToken(FCMToken);
        //             console.log(FCMToken);
        //         })
        //         .catch(err => {
        //             console.log(err);
        //         })

        //     messaging.onMessage(payload => {
        //         // console.log('ayo fam xdd');
        //         console.log(`Payload: ${payload}`);
        //     })
        // }

        // const register = async () => {
        //     await axios.post(
        //         'http://127.0.0.1:8080/testing-rabbit/set-registration-token/',
        //         {
        //             "registrationToken": registrationToken
        //         },
        //         {
        //             headers: {
        //                 token: token
        //             }
        //         }
        //     ).then(res => {
        //         console.log(res);
        //     })
        //         .catch(err => console.log(err))
        // }

        // console.log('lmao');
        if (connected) {
            messagesRef.current.scrollIntoView();
            ws.onmessage = (msg) => {
                // console.log('message hehe');
                console.log(msg)
                let newMessages = [...messages];
                msg = JSON.parse(msg.data);
                newMessages.push({
                    senderName: msg.senderName,
                    text: msg.text,
                    timeStamp: Date.parse(new Date())
                })
                newMessages.sort((a, b) => {
                    if (a.timeStamp < b.timeStamp) {
                        return -1
                    }
                    if (a.timeStamp > b.timeStamp) {
                        return 1
                    }
                    return 0;
                })
                setMessages(newMessages);
            }
        }
        messaging.getToken({
            vapidKey: 'BInx6M6lbCz-xhJu-HUzOiwECWTobyh85Xc7B1W3E9EYxRNVHkT4i6juYVDrVXrpK2fWNrHsiPf0DtVfH0zryr8'
        })
            .then((FCMToken) => {
                // console.log('aboos eedak yasta');
                setRegisterationToken(FCMToken);
                console.log(FCMToken);
            })
            .catch(err => {
                console.log(err);
            })

    }, [connected, messages, registrationToken])

    return (
        <div style={{ height: "70%", width: "50%", display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <Card className={classes.card}>
                <CardContent style={{ height: "75%", width: '100%', justifyContent: 'center', alignItems: 'center', display: 'flex', flexDirection: 'column' }}>
                    <Typography variant="h6" style={{ marginBottom: '0.5vw' }}>
                        Spotify Chat
                    </Typography>
                    <div style={{ height: '90%', width: '80%', border: '3px solid rgba(127, 127, 127, 0.5)', borderRadius: 15, overflowY: 'auto' }}>
                        {
                            messages.map((msg, key) => {
                                return (
                                    <div key={key} style={{ marginTop: '1vw', marginLeft: '1vw', marginBottom: '1vw' }}>
                                        <Typography style={{ display: 'inline-block', marginRight: '0.5vw', fontWeight: 'bold' }}>{msg.senderName}: </Typography>
                                        <Typography style={{ display: 'inline-block' }} variant="body1">
                                            {msg.text}
                                        </Typography>
                                    </div>
                                )
                            })
                        }
                        <div ref={messagesRef} />
                    </div>
                </CardContent>
                <CardActions style={{ width: "100%", display: "flex", flexDirection: "column" }}>
                    <TextField onChange={handleChange} id="outlined-basic" label="Enter your message" variant="outlined" style={{ width: "80%", marginBottom: "0.5vw" }} />
                    <div style={{ height: '40%', display: "flex", justifyContent: "center", width: "100%", flexDirection: "row" }}>
                        <Button color="primary" onClick={handleInit} style={{ marginRight: "1vw" }} disabled={!init}>Init</Button>
                        <Button color="primary" onClick={handleSend} style={{ marginRight: "1vw" }} disabled={!connected}>Send</Button>
                        <Button color="primary" onClick={handleCreate} disabled={connected} style={{ marginRight: "1vw" }}>Create</Button>
                        <Button color="primary" onClick={handleConnect} disabled={connected}>Connect</Button>
                    </div>
                </CardActions>
            </Card>
            <Snackbar open={open}
                onClose={handleClose}
                autoHideDuration={6000}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'left'
                }}>
                <Alert severity="info" action={
                    <React.Fragment>
                        <IconButton size="small" aria-label="close" color="inherit" onClick={handleClose}>
                            <CloseIcon fontSize="small" />
                        </IconButton>
                    </React.Fragment>
                }>
                    {`${notif.senderName}: ${notif.text}`}
                </Alert>
            </Snackbar>
        </div>
    )
}

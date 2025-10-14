import React from 'react';
import ReactDOM from 'react-dom/client';
import {Greeting} from './components/Greeting/Greeting.tsx';
import {ErrorState, LoadingState, SuccessState} from "shared";
import {whenMyScreenState} from "shared-generated/MyScreenState.ts"

alert(
    "STATE: " +
    whenMyScreenState(LoadingState.getInstance(), {
        errorState: (error) => `Error: ${error}`,
        else: (state) => `Not error: ${state}`,
        // loadingState: (_loading) => "Loading...",
        // successState: (success) => `Success: ${success}`
    }),
)
alert(
    "STATE: " +
    whenMyScreenState(LoadingState.getInstance(), {
        errorState: (error) => `Error: ${error}`,
        loadingState: (_loading) => "Loading...",
        successState: (success) => `Success: ${success}`
    }),
)
alert(
    "STATE: " +
    whenMyScreenState(new SuccessState("SUCCESS!!"), {
        errorState: (error) => `Error: ${error}`,
        loadingState: (_loading) => "Loading...",
        successState: (success) => `Success: ${success}`
    })
)

alert(
    "STATE: " +
    whenMyScreenState(new ErrorState(new DOMException("test")), {
        errorState: (error) => `Error: ${error}`,
        loadingState: (_loading) => "Loading...",
        successState: (success) => `Success: ${success}`
    })
)

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
        <Greeting/>
    </React.StrictMode>
);
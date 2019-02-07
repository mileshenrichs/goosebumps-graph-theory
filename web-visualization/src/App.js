import React, { Component } from 'react';
import './App.css';
import Header from './components/Header/Header';

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            vizOptions: [
                { value: 'default', label: 'Default Graph View'},
                { value: 'optimal-path', label: 'Optimal path (shortest path to best ending)' }
            ],
            selectedVizOption: { value: 'default', label: 'Default Graph View'}
        };
    }

    onVizButtonClick() {
        console.log('viz button clicked');
    }

    render() {
        return (
            <Header
                vizOptions={this.state.vizOptions}
                selectedVizOption={this.state.selectedVizOption}
                vizOptionChangeHandler={(option) => this.setState({selectedVizOption: option})}
                vizButtonClickHandler={() => this.onVizButtonClick()}
            />
        );
    }
}

export default App;

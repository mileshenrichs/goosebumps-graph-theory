import React from 'react';

const InfoPanel = ({ activeElement }) => {
    let elementContext;
    let elementInfo;

    if(activeElement) {
        if(activeElement.pageNo) {
            elementContext = 'page ' + activeElement.pageNo;
            elementInfo = activeElement.pageSummary;
            if(activeElement.utility) {
                elementInfo += ' (' + activeElement.utility + ')';
            }
        } else if(activeElement.source && activeElement.target) {
            elementContext = 'edge from pg. ' + activeElement.source.pageNo + ' to pg. ' + activeElement.target.pageNo;
            elementInfo = activeElement.decisionDesc || '';
        }
    }

    return (
        <div className="InfoPanel">
            {elementContext && <span className="InfoPanel__context">{elementContext}</span>}
            {elementInfo && <span className="InfoPanel__info">{elementInfo}</span>}
            {!activeElement && <span className="InfoPanel__blank">Hover over nodes and links to see details in this panel!</span>}
        </div>
    );
};

export default InfoPanel;
import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Generate a Project, Provider and Installation relationship',
    Svg: require('@site/static/img/undraw_connection.svg').default,
    description: (
      <>
        Associate Projects with Providers and create new installations in those correlations.
      </>
    ),
  },
  {
    title: 'Create Metric Definitions',
    Svg: require('@site/static/img/undraw_create.svg').default,
    description: (
      <>
      Create Metric Definitions describing a Metric.
      </>
    ),
  },
  {
    title: 'Send and receive Metrics',
    Svg: require('@site/static/img/undraw_data_processing.svg').default,
    description: (
      <>
        Create Metric Definitions describing a Metric.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}

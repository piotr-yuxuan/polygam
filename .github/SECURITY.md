# Security Policy

## Existing tooling

This repository is managed by
[Walter](https://github.com/piotr-yuxuan/walter-ci), a CICD system. It
enforces continuous vulnerability scans by the following tools:

- https://github.com/rm-hull/nvd-clojure
- https://github.com/clj-holmes/clj-holmes
- https://github.com/aquasecurity/tfsec
- https://github.com/aquasecurity/trivy

## Supported Versions

This message being present means that version `0.1.0`
has been scrutinised on commit `85bcf7da5868c7aac6d20f264c85185095c05ba7`. See `git` log for
history of supported versions.

## Known vulnerabilities

Vulnerabilities discovered by
[nvd-clojure](https://github.com/rm-hull/nvd-clojure) are publicly
disclosed in
[`./doc/known-vulnerabilities.csv`](./doc/known-vulnerabilities.csv). As
this repository is public and open-source, this is intended to inform
your choice whether to use it, or not to use it. Beware that not all
vulnerability can be exploited.

## Reporting a Vulnerability

Open an issue, or contact the [code owners](./CODEOWNERS.yml) on
social media.

# vericlig-fub

Code for the FUB VeriCliG project. The pipeline contained in this repository will train predictive models to detect
clinical acticities (therapies) in clinical documents. The models rely on distributional features extracted from UMLS 
Metathesaurus annotations.

The UMLS Metathesaurus and the SemRep corpus must be downloaded and installed separately, and the system paths updated.
Also, the Stanford CoreNLP NLP pipeline must be installed (the dependecy parser, with its associated tokenizer and 
NER modules).

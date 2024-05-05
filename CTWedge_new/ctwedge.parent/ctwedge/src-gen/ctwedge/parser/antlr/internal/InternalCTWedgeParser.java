package ctwedge.parser.antlr.internal;

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import ctwedge.services.CTWedgeGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class InternalCTWedgeParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_INT", "RULE_NUMID", "RULE_STRING", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'Model'", "'Parameters'", "':'", "'Constraints'", "';'", "'Boolean'", "'{'", "'TRUE'", "','", "'FALSE'", "'}'", "'true'", "'false'", "'['", "'..'", "']'", "'step'", "'#'", "'('", "')'", "'||'", "'or'", "'OR'", "'|'", "'&&'", "'and'", "'AND'", "'&'", "'!'", "'not'", "'NOT'", "'-'", "'>'", "'<'", "'>='", "'<='", "'=='", "'!='", "'='", "'+'", "'%'", "'*'", "'/'", "'=>'", "'<=>'", "'->'", "'<->'"
    };
    public static final int T__50=50;
    public static final int T__19=19;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__55=55;
    public static final int T__12=12;
    public static final int T__56=56;
    public static final int T__13=13;
    public static final int T__57=57;
    public static final int T__14=14;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int RULE_ID=4;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int RULE_INT=5;
    public static final int T__29=29;
    public static final int T__22=22;
    public static final int RULE_ML_COMMENT=8;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int RULE_STRING=7;
    public static final int RULE_NUMID=6;
    public static final int RULE_SL_COMMENT=9;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int EOF=-1;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int RULE_WS=10;
    public static final int RULE_ANY_OTHER=11;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;

    // delegates
    // delegators


        public InternalCTWedgeParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalCTWedgeParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalCTWedgeParser.tokenNames; }
    public String getGrammarFileName() { return "InternalCTWedge.g"; }



     	private CTWedgeGrammarAccess grammarAccess;

        public InternalCTWedgeParser(TokenStream input, CTWedgeGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }

        @Override
        protected String getFirstRuleName() {
        	return "CitModel";
       	}

       	@Override
       	protected CTWedgeGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}




    // $ANTLR start "entryRuleCitModel"
    // InternalCTWedge.g:65:1: entryRuleCitModel returns [EObject current=null] : iv_ruleCitModel= ruleCitModel EOF ;
    public final EObject entryRuleCitModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleCitModel = null;


        try {
            // InternalCTWedge.g:65:49: (iv_ruleCitModel= ruleCitModel EOF )
            // InternalCTWedge.g:66:2: iv_ruleCitModel= ruleCitModel EOF
            {
             newCompositeNode(grammarAccess.getCitModelRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleCitModel=ruleCitModel();

            state._fsp--;

             current =iv_ruleCitModel; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleCitModel"


    // $ANTLR start "ruleCitModel"
    // InternalCTWedge.g:72:1: ruleCitModel returns [EObject current=null] : ( () otherlv_1= 'Model' ( (lv_name_2_0= RULE_ID ) ) otherlv_3= 'Parameters' otherlv_4= ':' ( (lv_parameters_5_0= ruleParameter ) )+ (otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+ )? ) ;
    public final EObject ruleCitModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token lv_name_2_0=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        EObject lv_parameters_5_0 = null;

        EObject lv_constraints_8_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:78:2: ( ( () otherlv_1= 'Model' ( (lv_name_2_0= RULE_ID ) ) otherlv_3= 'Parameters' otherlv_4= ':' ( (lv_parameters_5_0= ruleParameter ) )+ (otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+ )? ) )
            // InternalCTWedge.g:79:2: ( () otherlv_1= 'Model' ( (lv_name_2_0= RULE_ID ) ) otherlv_3= 'Parameters' otherlv_4= ':' ( (lv_parameters_5_0= ruleParameter ) )+ (otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+ )? )
            {
            // InternalCTWedge.g:79:2: ( () otherlv_1= 'Model' ( (lv_name_2_0= RULE_ID ) ) otherlv_3= 'Parameters' otherlv_4= ':' ( (lv_parameters_5_0= ruleParameter ) )+ (otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+ )? )
            // InternalCTWedge.g:80:3: () otherlv_1= 'Model' ( (lv_name_2_0= RULE_ID ) ) otherlv_3= 'Parameters' otherlv_4= ':' ( (lv_parameters_5_0= ruleParameter ) )+ (otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+ )?
            {
            // InternalCTWedge.g:80:3: ()
            // InternalCTWedge.g:81:4: 
            {

            				current = forceCreateModelElement(
            					grammarAccess.getCitModelAccess().getCitModelAction_0(),
            					current);
            			

            }

            otherlv_1=(Token)match(input,12,FOLLOW_3); 

            			newLeafNode(otherlv_1, grammarAccess.getCitModelAccess().getModelKeyword_1());
            		
            // InternalCTWedge.g:91:3: ( (lv_name_2_0= RULE_ID ) )
            // InternalCTWedge.g:92:4: (lv_name_2_0= RULE_ID )
            {
            // InternalCTWedge.g:92:4: (lv_name_2_0= RULE_ID )
            // InternalCTWedge.g:93:5: lv_name_2_0= RULE_ID
            {
            lv_name_2_0=(Token)match(input,RULE_ID,FOLLOW_4); 

            					newLeafNode(lv_name_2_0, grammarAccess.getCitModelAccess().getNameIDTerminalRuleCall_2_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getCitModelRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_2_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_3=(Token)match(input,13,FOLLOW_5); 

            			newLeafNode(otherlv_3, grammarAccess.getCitModelAccess().getParametersKeyword_3());
            		
            otherlv_4=(Token)match(input,14,FOLLOW_3); 

            			newLeafNode(otherlv_4, grammarAccess.getCitModelAccess().getColonKeyword_4());
            		
            // InternalCTWedge.g:117:3: ( (lv_parameters_5_0= ruleParameter ) )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==RULE_ID) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // InternalCTWedge.g:118:4: (lv_parameters_5_0= ruleParameter )
            	    {
            	    // InternalCTWedge.g:118:4: (lv_parameters_5_0= ruleParameter )
            	    // InternalCTWedge.g:119:5: lv_parameters_5_0= ruleParameter
            	    {

            	    					newCompositeNode(grammarAccess.getCitModelAccess().getParametersParameterParserRuleCall_5_0());
            	    				
            	    pushFollow(FOLLOW_6);
            	    lv_parameters_5_0=ruleParameter();

            	    state._fsp--;


            	    					if (current==null) {
            	    						current = createModelElementForParent(grammarAccess.getCitModelRule());
            	    					}
            	    					add(
            	    						current,
            	    						"parameters",
            	    						lv_parameters_5_0,
            	    						"ctwedge.CTWedge.Parameter");
            	    					afterParserOrEnumRuleCall();
            	    				

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

            // InternalCTWedge.g:136:3: (otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+ )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==15) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // InternalCTWedge.g:137:4: otherlv_6= 'Constraints' otherlv_7= ':' ( (lv_constraints_8_0= ruleConstraint ) )+
                    {
                    otherlv_6=(Token)match(input,15,FOLLOW_5); 

                    				newLeafNode(otherlv_6, grammarAccess.getCitModelAccess().getConstraintsKeyword_6_0());
                    			
                    otherlv_7=(Token)match(input,14,FOLLOW_7); 

                    				newLeafNode(otherlv_7, grammarAccess.getCitModelAccess().getColonKeyword_6_1());
                    			
                    // InternalCTWedge.g:145:4: ( (lv_constraints_8_0= ruleConstraint ) )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==29) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // InternalCTWedge.g:146:5: (lv_constraints_8_0= ruleConstraint )
                    	    {
                    	    // InternalCTWedge.g:146:5: (lv_constraints_8_0= ruleConstraint )
                    	    // InternalCTWedge.g:147:6: lv_constraints_8_0= ruleConstraint
                    	    {

                    	    						newCompositeNode(grammarAccess.getCitModelAccess().getConstraintsConstraintParserRuleCall_6_2_0());
                    	    					
                    	    pushFollow(FOLLOW_8);
                    	    lv_constraints_8_0=ruleConstraint();

                    	    state._fsp--;


                    	    						if (current==null) {
                    	    							current = createModelElementForParent(grammarAccess.getCitModelRule());
                    	    						}
                    	    						add(
                    	    							current,
                    	    							"constraints",
                    	    							lv_constraints_8_0,
                    	    							"ctwedge.CTWedge.Constraint");
                    	    						afterParserOrEnumRuleCall();
                    	    					

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleCitModel"


    // $ANTLR start "entryRuleParameter"
    // InternalCTWedge.g:169:1: entryRuleParameter returns [EObject current=null] : iv_ruleParameter= ruleParameter EOF ;
    public final EObject entryRuleParameter() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleParameter = null;


        try {
            // InternalCTWedge.g:169:50: (iv_ruleParameter= ruleParameter EOF )
            // InternalCTWedge.g:170:2: iv_ruleParameter= ruleParameter EOF
            {
             newCompositeNode(grammarAccess.getParameterRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleParameter=ruleParameter();

            state._fsp--;

             current =iv_ruleParameter; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleParameter"


    // $ANTLR start "ruleParameter"
    // InternalCTWedge.g:176:1: ruleParameter returns [EObject current=null] : ( (this_Bool_0= ruleBool | this_Enumerative_1= ruleEnumerative | this_Range_2= ruleRange ) (otherlv_3= ';' )? ) ;
    public final EObject ruleParameter() throws RecognitionException {
        EObject current = null;

        Token otherlv_3=null;
        EObject this_Bool_0 = null;

        EObject this_Enumerative_1 = null;

        EObject this_Range_2 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:182:2: ( ( (this_Bool_0= ruleBool | this_Enumerative_1= ruleEnumerative | this_Range_2= ruleRange ) (otherlv_3= ';' )? ) )
            // InternalCTWedge.g:183:2: ( (this_Bool_0= ruleBool | this_Enumerative_1= ruleEnumerative | this_Range_2= ruleRange ) (otherlv_3= ';' )? )
            {
            // InternalCTWedge.g:183:2: ( (this_Bool_0= ruleBool | this_Enumerative_1= ruleEnumerative | this_Range_2= ruleRange ) (otherlv_3= ';' )? )
            // InternalCTWedge.g:184:3: (this_Bool_0= ruleBool | this_Enumerative_1= ruleEnumerative | this_Range_2= ruleRange ) (otherlv_3= ';' )?
            {
            // InternalCTWedge.g:184:3: (this_Bool_0= ruleBool | this_Enumerative_1= ruleEnumerative | this_Range_2= ruleRange )
            int alt4=3;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==RULE_ID) ) {
                int LA4_1 = input.LA(2);

                if ( (LA4_1==14) ) {
                    switch ( input.LA(3) ) {
                    case 17:
                        {
                        alt4=1;
                        }
                        break;
                    case 18:
                        {
                        int LA4_4 = input.LA(4);

                        if ( ((LA4_4>=RULE_ID && LA4_4<=RULE_STRING)||LA4_4==43) ) {
                            alt4=2;
                        }
                        else if ( (LA4_4==19||LA4_4==21||(LA4_4>=23 && LA4_4<=24)) ) {
                            alt4=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 4, 4, input);

                            throw nvae;
                        }
                        }
                        break;
                    case 25:
                        {
                        alt4=3;
                        }
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 2, input);

                        throw nvae;
                    }

                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // InternalCTWedge.g:185:4: this_Bool_0= ruleBool
                    {

                    				newCompositeNode(grammarAccess.getParameterAccess().getBoolParserRuleCall_0_0());
                    			
                    pushFollow(FOLLOW_9);
                    this_Bool_0=ruleBool();

                    state._fsp--;


                    				current = this_Bool_0;
                    				afterParserOrEnumRuleCall();
                    			

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:194:4: this_Enumerative_1= ruleEnumerative
                    {

                    				newCompositeNode(grammarAccess.getParameterAccess().getEnumerativeParserRuleCall_0_1());
                    			
                    pushFollow(FOLLOW_9);
                    this_Enumerative_1=ruleEnumerative();

                    state._fsp--;


                    				current = this_Enumerative_1;
                    				afterParserOrEnumRuleCall();
                    			

                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:203:4: this_Range_2= ruleRange
                    {

                    				newCompositeNode(grammarAccess.getParameterAccess().getRangeParserRuleCall_0_2());
                    			
                    pushFollow(FOLLOW_9);
                    this_Range_2=ruleRange();

                    state._fsp--;


                    				current = this_Range_2;
                    				afterParserOrEnumRuleCall();
                    			

                    }
                    break;

            }

            // InternalCTWedge.g:212:3: (otherlv_3= ';' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==16) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // InternalCTWedge.g:213:4: otherlv_3= ';'
                    {
                    otherlv_3=(Token)match(input,16,FOLLOW_2); 

                    				newLeafNode(otherlv_3, grammarAccess.getParameterAccess().getSemicolonKeyword_1());
                    			

                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleParameter"


    // $ANTLR start "entryRuleBool"
    // InternalCTWedge.g:222:1: entryRuleBool returns [EObject current=null] : iv_ruleBool= ruleBool EOF ;
    public final EObject entryRuleBool() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBool = null;


        try {
            // InternalCTWedge.g:222:45: (iv_ruleBool= ruleBool EOF )
            // InternalCTWedge.g:223:2: iv_ruleBool= ruleBool EOF
            {
             newCompositeNode(grammarAccess.getBoolRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleBool=ruleBool();

            state._fsp--;

             current =iv_ruleBool; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBool"


    // $ANTLR start "ruleBool"
    // InternalCTWedge.g:229:1: ruleBool returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' (otherlv_2= 'Boolean' | (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' ) | (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' ) | (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' ) | (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' ) ) ) ;
    public final EObject ruleBool() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token otherlv_8=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token otherlv_12=null;
        Token otherlv_13=null;
        Token otherlv_14=null;
        Token otherlv_15=null;
        Token otherlv_16=null;
        Token otherlv_17=null;
        Token otherlv_18=null;
        Token otherlv_19=null;
        Token otherlv_20=null;
        Token otherlv_21=null;
        Token otherlv_22=null;


        	enterRule();

        try {
            // InternalCTWedge.g:235:2: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' (otherlv_2= 'Boolean' | (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' ) | (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' ) | (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' ) | (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' ) ) ) )
            // InternalCTWedge.g:236:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' (otherlv_2= 'Boolean' | (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' ) | (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' ) | (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' ) | (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' ) ) )
            {
            // InternalCTWedge.g:236:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' (otherlv_2= 'Boolean' | (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' ) | (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' ) | (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' ) | (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' ) ) )
            // InternalCTWedge.g:237:3: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' (otherlv_2= 'Boolean' | (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' ) | (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' ) | (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' ) | (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' ) )
            {
            // InternalCTWedge.g:237:3: ( (lv_name_0_0= RULE_ID ) )
            // InternalCTWedge.g:238:4: (lv_name_0_0= RULE_ID )
            {
            // InternalCTWedge.g:238:4: (lv_name_0_0= RULE_ID )
            // InternalCTWedge.g:239:5: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_5); 

            					newLeafNode(lv_name_0_0, grammarAccess.getBoolAccess().getNameIDTerminalRuleCall_0_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getBoolRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_0_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_1=(Token)match(input,14,FOLLOW_10); 

            			newLeafNode(otherlv_1, grammarAccess.getBoolAccess().getColonKeyword_1());
            		
            // InternalCTWedge.g:259:3: (otherlv_2= 'Boolean' | (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' ) | (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' ) | (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' ) | (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' ) )
            int alt10=5;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==17) ) {
                alt10=1;
            }
            else if ( (LA10_0==18) ) {
                switch ( input.LA(2) ) {
                case 21:
                    {
                    alt10=3;
                    }
                    break;
                case 24:
                    {
                    alt10=5;
                    }
                    break;
                case 19:
                    {
                    alt10=2;
                    }
                    break;
                case 23:
                    {
                    alt10=4;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 2, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // InternalCTWedge.g:260:4: otherlv_2= 'Boolean'
                    {
                    otherlv_2=(Token)match(input,17,FOLLOW_2); 

                    				newLeafNode(otherlv_2, grammarAccess.getBoolAccess().getBooleanKeyword_2_0());
                    			

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:265:4: (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' )
                    {
                    // InternalCTWedge.g:265:4: (otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}' )
                    // InternalCTWedge.g:266:5: otherlv_3= '{' otherlv_4= 'TRUE' (otherlv_5= ',' )? otherlv_6= 'FALSE' otherlv_7= '}'
                    {
                    otherlv_3=(Token)match(input,18,FOLLOW_11); 

                    					newLeafNode(otherlv_3, grammarAccess.getBoolAccess().getLeftCurlyBracketKeyword_2_1_0());
                    				
                    otherlv_4=(Token)match(input,19,FOLLOW_12); 

                    					newLeafNode(otherlv_4, grammarAccess.getBoolAccess().getTRUEKeyword_2_1_1());
                    				
                    // InternalCTWedge.g:274:5: (otherlv_5= ',' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==20) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // InternalCTWedge.g:275:6: otherlv_5= ','
                            {
                            otherlv_5=(Token)match(input,20,FOLLOW_13); 

                            						newLeafNode(otherlv_5, grammarAccess.getBoolAccess().getCommaKeyword_2_1_2());
                            					

                            }
                            break;

                    }

                    otherlv_6=(Token)match(input,21,FOLLOW_14); 

                    					newLeafNode(otherlv_6, grammarAccess.getBoolAccess().getFALSEKeyword_2_1_3());
                    				
                    otherlv_7=(Token)match(input,22,FOLLOW_2); 

                    					newLeafNode(otherlv_7, grammarAccess.getBoolAccess().getRightCurlyBracketKeyword_2_1_4());
                    				

                    }


                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:290:4: (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' )
                    {
                    // InternalCTWedge.g:290:4: (otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}' )
                    // InternalCTWedge.g:291:5: otherlv_8= '{' otherlv_9= 'FALSE' (otherlv_10= ',' )? otherlv_11= 'TRUE' otherlv_12= '}'
                    {
                    otherlv_8=(Token)match(input,18,FOLLOW_13); 

                    					newLeafNode(otherlv_8, grammarAccess.getBoolAccess().getLeftCurlyBracketKeyword_2_2_0());
                    				
                    otherlv_9=(Token)match(input,21,FOLLOW_15); 

                    					newLeafNode(otherlv_9, grammarAccess.getBoolAccess().getFALSEKeyword_2_2_1());
                    				
                    // InternalCTWedge.g:299:5: (otherlv_10= ',' )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==20) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // InternalCTWedge.g:300:6: otherlv_10= ','
                            {
                            otherlv_10=(Token)match(input,20,FOLLOW_11); 

                            						newLeafNode(otherlv_10, grammarAccess.getBoolAccess().getCommaKeyword_2_2_2());
                            					

                            }
                            break;

                    }

                    otherlv_11=(Token)match(input,19,FOLLOW_14); 

                    					newLeafNode(otherlv_11, grammarAccess.getBoolAccess().getTRUEKeyword_2_2_3());
                    				
                    otherlv_12=(Token)match(input,22,FOLLOW_2); 

                    					newLeafNode(otherlv_12, grammarAccess.getBoolAccess().getRightCurlyBracketKeyword_2_2_4());
                    				

                    }


                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:315:4: (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' )
                    {
                    // InternalCTWedge.g:315:4: (otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}' )
                    // InternalCTWedge.g:316:5: otherlv_13= '{' otherlv_14= 'true' (otherlv_15= ',' )? otherlv_16= 'false' otherlv_17= '}'
                    {
                    otherlv_13=(Token)match(input,18,FOLLOW_16); 

                    					newLeafNode(otherlv_13, grammarAccess.getBoolAccess().getLeftCurlyBracketKeyword_2_3_0());
                    				
                    otherlv_14=(Token)match(input,23,FOLLOW_17); 

                    					newLeafNode(otherlv_14, grammarAccess.getBoolAccess().getTrueKeyword_2_3_1());
                    				
                    // InternalCTWedge.g:324:5: (otherlv_15= ',' )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==20) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // InternalCTWedge.g:325:6: otherlv_15= ','
                            {
                            otherlv_15=(Token)match(input,20,FOLLOW_18); 

                            						newLeafNode(otherlv_15, grammarAccess.getBoolAccess().getCommaKeyword_2_3_2());
                            					

                            }
                            break;

                    }

                    otherlv_16=(Token)match(input,24,FOLLOW_14); 

                    					newLeafNode(otherlv_16, grammarAccess.getBoolAccess().getFalseKeyword_2_3_3());
                    				
                    otherlv_17=(Token)match(input,22,FOLLOW_2); 

                    					newLeafNode(otherlv_17, grammarAccess.getBoolAccess().getRightCurlyBracketKeyword_2_3_4());
                    				

                    }


                    }
                    break;
                case 5 :
                    // InternalCTWedge.g:340:4: (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' )
                    {
                    // InternalCTWedge.g:340:4: (otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}' )
                    // InternalCTWedge.g:341:5: otherlv_18= '{' otherlv_19= 'false' (otherlv_20= ',' )? otherlv_21= 'true' otherlv_22= '}'
                    {
                    otherlv_18=(Token)match(input,18,FOLLOW_18); 

                    					newLeafNode(otherlv_18, grammarAccess.getBoolAccess().getLeftCurlyBracketKeyword_2_4_0());
                    				
                    otherlv_19=(Token)match(input,24,FOLLOW_19); 

                    					newLeafNode(otherlv_19, grammarAccess.getBoolAccess().getFalseKeyword_2_4_1());
                    				
                    // InternalCTWedge.g:349:5: (otherlv_20= ',' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==20) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // InternalCTWedge.g:350:6: otherlv_20= ','
                            {
                            otherlv_20=(Token)match(input,20,FOLLOW_16); 

                            						newLeafNode(otherlv_20, grammarAccess.getBoolAccess().getCommaKeyword_2_4_2());
                            					

                            }
                            break;

                    }

                    otherlv_21=(Token)match(input,23,FOLLOW_14); 

                    					newLeafNode(otherlv_21, grammarAccess.getBoolAccess().getTrueKeyword_2_4_3());
                    				
                    otherlv_22=(Token)match(input,22,FOLLOW_2); 

                    					newLeafNode(otherlv_22, grammarAccess.getBoolAccess().getRightCurlyBracketKeyword_2_4_4());
                    				

                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBool"


    // $ANTLR start "entryRuleEnumerative"
    // InternalCTWedge.g:369:1: entryRuleEnumerative returns [EObject current=null] : iv_ruleEnumerative= ruleEnumerative EOF ;
    public final EObject entryRuleEnumerative() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEnumerative = null;


        try {
            // InternalCTWedge.g:369:52: (iv_ruleEnumerative= ruleEnumerative EOF )
            // InternalCTWedge.g:370:2: iv_ruleEnumerative= ruleEnumerative EOF
            {
             newCompositeNode(grammarAccess.getEnumerativeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEnumerative=ruleEnumerative();

            state._fsp--;

             current =iv_ruleEnumerative; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEnumerative"


    // $ANTLR start "ruleEnumerative"
    // InternalCTWedge.g:376:1: ruleEnumerative returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) (otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}' ) ) ;
    public final EObject ruleEnumerative() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        EObject lv_elements_3_0 = null;

        EObject lv_elements_5_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:382:2: ( ( ( (lv_name_0_0= RULE_ID ) ) (otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}' ) ) )
            // InternalCTWedge.g:383:2: ( ( (lv_name_0_0= RULE_ID ) ) (otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}' ) )
            {
            // InternalCTWedge.g:383:2: ( ( (lv_name_0_0= RULE_ID ) ) (otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}' ) )
            // InternalCTWedge.g:384:3: ( (lv_name_0_0= RULE_ID ) ) (otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}' )
            {
            // InternalCTWedge.g:384:3: ( (lv_name_0_0= RULE_ID ) )
            // InternalCTWedge.g:385:4: (lv_name_0_0= RULE_ID )
            {
            // InternalCTWedge.g:385:4: (lv_name_0_0= RULE_ID )
            // InternalCTWedge.g:386:5: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_5); 

            					newLeafNode(lv_name_0_0, grammarAccess.getEnumerativeAccess().getNameIDTerminalRuleCall_0_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getEnumerativeRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_0_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            // InternalCTWedge.g:402:3: (otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}' )
            // InternalCTWedge.g:403:4: otherlv_1= ':' otherlv_2= '{' ( (lv_elements_3_0= ruleElement ) ) ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )* otherlv_6= '}'
            {
            otherlv_1=(Token)match(input,14,FOLLOW_20); 

            				newLeafNode(otherlv_1, grammarAccess.getEnumerativeAccess().getColonKeyword_1_0());
            			
            otherlv_2=(Token)match(input,18,FOLLOW_21); 

            				newLeafNode(otherlv_2, grammarAccess.getEnumerativeAccess().getLeftCurlyBracketKeyword_1_1());
            			
            // InternalCTWedge.g:411:4: ( (lv_elements_3_0= ruleElement ) )
            // InternalCTWedge.g:412:5: (lv_elements_3_0= ruleElement )
            {
            // InternalCTWedge.g:412:5: (lv_elements_3_0= ruleElement )
            // InternalCTWedge.g:413:6: lv_elements_3_0= ruleElement
            {

            						newCompositeNode(grammarAccess.getEnumerativeAccess().getElementsElementParserRuleCall_1_2_0());
            					
            pushFollow(FOLLOW_22);
            lv_elements_3_0=ruleElement();

            state._fsp--;


            						if (current==null) {
            							current = createModelElementForParent(grammarAccess.getEnumerativeRule());
            						}
            						add(
            							current,
            							"elements",
            							lv_elements_3_0,
            							"ctwedge.CTWedge.Element");
            						afterParserOrEnumRuleCall();
            					

            }


            }

            // InternalCTWedge.g:430:4: ( (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>=RULE_ID && LA12_0<=RULE_STRING)||LA12_0==20||LA12_0==43) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // InternalCTWedge.g:431:5: (otherlv_4= ',' )? ( (lv_elements_5_0= ruleElement ) )
            	    {
            	    // InternalCTWedge.g:431:5: (otherlv_4= ',' )?
            	    int alt11=2;
            	    int LA11_0 = input.LA(1);

            	    if ( (LA11_0==20) ) {
            	        alt11=1;
            	    }
            	    switch (alt11) {
            	        case 1 :
            	            // InternalCTWedge.g:432:6: otherlv_4= ','
            	            {
            	            otherlv_4=(Token)match(input,20,FOLLOW_21); 

            	            						newLeafNode(otherlv_4, grammarAccess.getEnumerativeAccess().getCommaKeyword_1_3_0());
            	            					

            	            }
            	            break;

            	    }

            	    // InternalCTWedge.g:437:5: ( (lv_elements_5_0= ruleElement ) )
            	    // InternalCTWedge.g:438:6: (lv_elements_5_0= ruleElement )
            	    {
            	    // InternalCTWedge.g:438:6: (lv_elements_5_0= ruleElement )
            	    // InternalCTWedge.g:439:7: lv_elements_5_0= ruleElement
            	    {

            	    							newCompositeNode(grammarAccess.getEnumerativeAccess().getElementsElementParserRuleCall_1_3_1_0());
            	    						
            	    pushFollow(FOLLOW_22);
            	    lv_elements_5_0=ruleElement();

            	    state._fsp--;


            	    							if (current==null) {
            	    								current = createModelElementForParent(grammarAccess.getEnumerativeRule());
            	    							}
            	    							add(
            	    								current,
            	    								"elements",
            	    								lv_elements_5_0,
            	    								"ctwedge.CTWedge.Element");
            	    							afterParserOrEnumRuleCall();
            	    						

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            otherlv_6=(Token)match(input,22,FOLLOW_2); 

            				newLeafNode(otherlv_6, grammarAccess.getEnumerativeAccess().getRightCurlyBracketKeyword_1_4());
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEnumerative"


    // $ANTLR start "entryRuleElement"
    // InternalCTWedge.g:466:1: entryRuleElement returns [EObject current=null] : iv_ruleElement= ruleElement EOF ;
    public final EObject entryRuleElement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleElement = null;


        try {
            // InternalCTWedge.g:466:48: (iv_ruleElement= ruleElement EOF )
            // InternalCTWedge.g:467:2: iv_ruleElement= ruleElement EOF
            {
             newCompositeNode(grammarAccess.getElementRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleElement=ruleElement();

            state._fsp--;

             current =iv_ruleElement; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleElement"


    // $ANTLR start "ruleElement"
    // InternalCTWedge.g:473:1: ruleElement returns [EObject current=null] : ( (lv_name_0_0= ruleelementID ) ) ;
    public final EObject ruleElement() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_name_0_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:479:2: ( ( (lv_name_0_0= ruleelementID ) ) )
            // InternalCTWedge.g:480:2: ( (lv_name_0_0= ruleelementID ) )
            {
            // InternalCTWedge.g:480:2: ( (lv_name_0_0= ruleelementID ) )
            // InternalCTWedge.g:481:3: (lv_name_0_0= ruleelementID )
            {
            // InternalCTWedge.g:481:3: (lv_name_0_0= ruleelementID )
            // InternalCTWedge.g:482:4: lv_name_0_0= ruleelementID
            {

            				newCompositeNode(grammarAccess.getElementAccess().getNameElementIDParserRuleCall_0());
            			
            pushFollow(FOLLOW_2);
            lv_name_0_0=ruleelementID();

            state._fsp--;


            				if (current==null) {
            					current = createModelElementForParent(grammarAccess.getElementRule());
            				}
            				set(
            					current,
            					"name",
            					lv_name_0_0,
            					"ctwedge.CTWedge.elementID");
            				afterParserOrEnumRuleCall();
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleElement"


    // $ANTLR start "entryRuleRange"
    // InternalCTWedge.g:502:1: entryRuleRange returns [EObject current=null] : iv_ruleRange= ruleRange EOF ;
    public final EObject entryRuleRange() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRange = null;


        try {
            // InternalCTWedge.g:502:46: (iv_ruleRange= ruleRange EOF )
            // InternalCTWedge.g:503:2: iv_ruleRange= ruleRange EOF
            {
             newCompositeNode(grammarAccess.getRangeRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRange=ruleRange();

            state._fsp--;

             current =iv_ruleRange; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRange"


    // $ANTLR start "ruleRange"
    // InternalCTWedge.g:509:1: ruleRange returns [EObject current=null] : ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' otherlv_2= '[' ( (lv_begin_3_0= rulePossiblySignedNumber ) ) otherlv_4= '..' ( (lv_end_5_0= rulePossiblySignedNumber ) ) otherlv_6= ']' (otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) ) )? ) ;
    public final EObject ruleRange() throws RecognitionException {
        EObject current = null;

        Token lv_name_0_0=null;
        Token otherlv_1=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        Token otherlv_7=null;
        Token lv_step_8_0=null;
        AntlrDatatypeRuleToken lv_begin_3_0 = null;

        AntlrDatatypeRuleToken lv_end_5_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:515:2: ( ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' otherlv_2= '[' ( (lv_begin_3_0= rulePossiblySignedNumber ) ) otherlv_4= '..' ( (lv_end_5_0= rulePossiblySignedNumber ) ) otherlv_6= ']' (otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) ) )? ) )
            // InternalCTWedge.g:516:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' otherlv_2= '[' ( (lv_begin_3_0= rulePossiblySignedNumber ) ) otherlv_4= '..' ( (lv_end_5_0= rulePossiblySignedNumber ) ) otherlv_6= ']' (otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) ) )? )
            {
            // InternalCTWedge.g:516:2: ( ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' otherlv_2= '[' ( (lv_begin_3_0= rulePossiblySignedNumber ) ) otherlv_4= '..' ( (lv_end_5_0= rulePossiblySignedNumber ) ) otherlv_6= ']' (otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) ) )? )
            // InternalCTWedge.g:517:3: ( (lv_name_0_0= RULE_ID ) ) otherlv_1= ':' otherlv_2= '[' ( (lv_begin_3_0= rulePossiblySignedNumber ) ) otherlv_4= '..' ( (lv_end_5_0= rulePossiblySignedNumber ) ) otherlv_6= ']' (otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) ) )?
            {
            // InternalCTWedge.g:517:3: ( (lv_name_0_0= RULE_ID ) )
            // InternalCTWedge.g:518:4: (lv_name_0_0= RULE_ID )
            {
            // InternalCTWedge.g:518:4: (lv_name_0_0= RULE_ID )
            // InternalCTWedge.g:519:5: lv_name_0_0= RULE_ID
            {
            lv_name_0_0=(Token)match(input,RULE_ID,FOLLOW_5); 

            					newLeafNode(lv_name_0_0, grammarAccess.getRangeAccess().getNameIDTerminalRuleCall_0_0());
            				

            					if (current==null) {
            						current = createModelElement(grammarAccess.getRangeRule());
            					}
            					setWithLastConsumed(
            						current,
            						"name",
            						lv_name_0_0,
            						"org.eclipse.xtext.common.Terminals.ID");
            				

            }


            }

            otherlv_1=(Token)match(input,14,FOLLOW_23); 

            			newLeafNode(otherlv_1, grammarAccess.getRangeAccess().getColonKeyword_1());
            		
            otherlv_2=(Token)match(input,25,FOLLOW_24); 

            			newLeafNode(otherlv_2, grammarAccess.getRangeAccess().getLeftSquareBracketKeyword_2());
            		
            // InternalCTWedge.g:543:3: ( (lv_begin_3_0= rulePossiblySignedNumber ) )
            // InternalCTWedge.g:544:4: (lv_begin_3_0= rulePossiblySignedNumber )
            {
            // InternalCTWedge.g:544:4: (lv_begin_3_0= rulePossiblySignedNumber )
            // InternalCTWedge.g:545:5: lv_begin_3_0= rulePossiblySignedNumber
            {

            					newCompositeNode(grammarAccess.getRangeAccess().getBeginPossiblySignedNumberParserRuleCall_3_0());
            				
            pushFollow(FOLLOW_25);
            lv_begin_3_0=rulePossiblySignedNumber();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getRangeRule());
            					}
            					set(
            						current,
            						"begin",
            						lv_begin_3_0,
            						"ctwedge.CTWedge.PossiblySignedNumber");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_4=(Token)match(input,26,FOLLOW_24); 

            			newLeafNode(otherlv_4, grammarAccess.getRangeAccess().getFullStopFullStopKeyword_4());
            		
            // InternalCTWedge.g:566:3: ( (lv_end_5_0= rulePossiblySignedNumber ) )
            // InternalCTWedge.g:567:4: (lv_end_5_0= rulePossiblySignedNumber )
            {
            // InternalCTWedge.g:567:4: (lv_end_5_0= rulePossiblySignedNumber )
            // InternalCTWedge.g:568:5: lv_end_5_0= rulePossiblySignedNumber
            {

            					newCompositeNode(grammarAccess.getRangeAccess().getEndPossiblySignedNumberParserRuleCall_5_0());
            				
            pushFollow(FOLLOW_26);
            lv_end_5_0=rulePossiblySignedNumber();

            state._fsp--;


            					if (current==null) {
            						current = createModelElementForParent(grammarAccess.getRangeRule());
            					}
            					set(
            						current,
            						"end",
            						lv_end_5_0,
            						"ctwedge.CTWedge.PossiblySignedNumber");
            					afterParserOrEnumRuleCall();
            				

            }


            }

            otherlv_6=(Token)match(input,27,FOLLOW_27); 

            			newLeafNode(otherlv_6, grammarAccess.getRangeAccess().getRightSquareBracketKeyword_6());
            		
            // InternalCTWedge.g:589:3: (otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==28) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // InternalCTWedge.g:590:4: otherlv_7= 'step' ( (lv_step_8_0= RULE_INT ) )
                    {
                    otherlv_7=(Token)match(input,28,FOLLOW_28); 

                    				newLeafNode(otherlv_7, grammarAccess.getRangeAccess().getStepKeyword_7_0());
                    			
                    // InternalCTWedge.g:594:4: ( (lv_step_8_0= RULE_INT ) )
                    // InternalCTWedge.g:595:5: (lv_step_8_0= RULE_INT )
                    {
                    // InternalCTWedge.g:595:5: (lv_step_8_0= RULE_INT )
                    // InternalCTWedge.g:596:6: lv_step_8_0= RULE_INT
                    {
                    lv_step_8_0=(Token)match(input,RULE_INT,FOLLOW_2); 

                    						newLeafNode(lv_step_8_0, grammarAccess.getRangeAccess().getStepINTTerminalRuleCall_7_1_0());
                    					

                    						if (current==null) {
                    							current = createModelElement(grammarAccess.getRangeRule());
                    						}
                    						setWithLastConsumed(
                    							current,
                    							"step",
                    							lv_step_8_0,
                    							"org.eclipse.xtext.common.Terminals.INT");
                    					

                    }


                    }


                    }
                    break;

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRange"


    // $ANTLR start "entryRuleConstraint"
    // InternalCTWedge.g:617:1: entryRuleConstraint returns [EObject current=null] : iv_ruleConstraint= ruleConstraint EOF ;
    public final EObject entryRuleConstraint() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleConstraint = null;


        try {
            // InternalCTWedge.g:617:51: (iv_ruleConstraint= ruleConstraint EOF )
            // InternalCTWedge.g:618:2: iv_ruleConstraint= ruleConstraint EOF
            {
             newCompositeNode(grammarAccess.getConstraintRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleConstraint=ruleConstraint();

            state._fsp--;

             current =iv_ruleConstraint; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleConstraint"


    // $ANTLR start "ruleConstraint"
    // InternalCTWedge.g:624:1: ruleConstraint returns [EObject current=null] : (otherlv_0= '#' this_ImpliesExpression_1= ruleImpliesExpression otherlv_2= '#' ) ;
    public final EObject ruleConstraint() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token otherlv_2=null;
        EObject this_ImpliesExpression_1 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:630:2: ( (otherlv_0= '#' this_ImpliesExpression_1= ruleImpliesExpression otherlv_2= '#' ) )
            // InternalCTWedge.g:631:2: (otherlv_0= '#' this_ImpliesExpression_1= ruleImpliesExpression otherlv_2= '#' )
            {
            // InternalCTWedge.g:631:2: (otherlv_0= '#' this_ImpliesExpression_1= ruleImpliesExpression otherlv_2= '#' )
            // InternalCTWedge.g:632:3: otherlv_0= '#' this_ImpliesExpression_1= ruleImpliesExpression otherlv_2= '#'
            {
            otherlv_0=(Token)match(input,29,FOLLOW_29); 

            			newLeafNode(otherlv_0, grammarAccess.getConstraintAccess().getNumberSignKeyword_0());
            		

            			newCompositeNode(grammarAccess.getConstraintAccess().getImpliesExpressionParserRuleCall_1());
            		
            pushFollow(FOLLOW_7);
            this_ImpliesExpression_1=ruleImpliesExpression();

            state._fsp--;


            			current = this_ImpliesExpression_1;
            			afterParserOrEnumRuleCall();
            		
            otherlv_2=(Token)match(input,29,FOLLOW_2); 

            			newLeafNode(otherlv_2, grammarAccess.getConstraintAccess().getNumberSignKeyword_2());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleConstraint"


    // $ANTLR start "entryRuleImpliesExpression"
    // InternalCTWedge.g:652:1: entryRuleImpliesExpression returns [EObject current=null] : iv_ruleImpliesExpression= ruleImpliesExpression EOF ;
    public final EObject entryRuleImpliesExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImpliesExpression = null;


        try {
            // InternalCTWedge.g:652:58: (iv_ruleImpliesExpression= ruleImpliesExpression EOF )
            // InternalCTWedge.g:653:2: iv_ruleImpliesExpression= ruleImpliesExpression EOF
            {
             newCompositeNode(grammarAccess.getImpliesExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleImpliesExpression=ruleImpliesExpression();

            state._fsp--;

             current =iv_ruleImpliesExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleImpliesExpression"


    // $ANTLR start "ruleImpliesExpression"
    // InternalCTWedge.g:659:1: ruleImpliesExpression returns [EObject current=null] : (this_OrExpression_0= ruleOrExpression ( () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) ) )* ) ;
    public final EObject ruleImpliesExpression() throws RecognitionException {
        EObject current = null;

        EObject this_OrExpression_0 = null;

        Enumerator lv_op_2_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:665:2: ( (this_OrExpression_0= ruleOrExpression ( () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) ) )* ) )
            // InternalCTWedge.g:666:2: (this_OrExpression_0= ruleOrExpression ( () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) ) )* )
            {
            // InternalCTWedge.g:666:2: (this_OrExpression_0= ruleOrExpression ( () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) ) )* )
            // InternalCTWedge.g:667:3: this_OrExpression_0= ruleOrExpression ( () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) ) )*
            {

            			newCompositeNode(grammarAccess.getImpliesExpressionAccess().getOrExpressionParserRuleCall_0());
            		
            pushFollow(FOLLOW_30);
            this_OrExpression_0=ruleOrExpression();

            state._fsp--;


            			current = this_OrExpression_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:675:3: ( () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>=55 && LA14_0<=58)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // InternalCTWedge.g:676:4: () ( (lv_op_2_0= ruleImpliesOperator ) ) ( (lv_right_3_0= ruleOrExpression ) )
            	    {
            	    // InternalCTWedge.g:676:4: ()
            	    // InternalCTWedge.g:677:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getImpliesExpressionAccess().getImpliesExpressionLeftAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalCTWedge.g:683:4: ( (lv_op_2_0= ruleImpliesOperator ) )
            	    // InternalCTWedge.g:684:5: (lv_op_2_0= ruleImpliesOperator )
            	    {
            	    // InternalCTWedge.g:684:5: (lv_op_2_0= ruleImpliesOperator )
            	    // InternalCTWedge.g:685:6: lv_op_2_0= ruleImpliesOperator
            	    {

            	    						newCompositeNode(grammarAccess.getImpliesExpressionAccess().getOpImpliesOperatorEnumRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_29);
            	    lv_op_2_0=ruleImpliesOperator();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getImpliesExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"op",
            	    							lv_op_2_0,
            	    							"ctwedge.CTWedge.ImpliesOperator");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }

            	    // InternalCTWedge.g:702:4: ( (lv_right_3_0= ruleOrExpression ) )
            	    // InternalCTWedge.g:703:5: (lv_right_3_0= ruleOrExpression )
            	    {
            	    // InternalCTWedge.g:703:5: (lv_right_3_0= ruleOrExpression )
            	    // InternalCTWedge.g:704:6: lv_right_3_0= ruleOrExpression
            	    {

            	    						newCompositeNode(grammarAccess.getImpliesExpressionAccess().getRightOrExpressionParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_30);
            	    lv_right_3_0=ruleOrExpression();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getImpliesExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.OrExpression");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleImpliesExpression"


    // $ANTLR start "entryRuleOrExpression"
    // InternalCTWedge.g:726:1: entryRuleOrExpression returns [EObject current=null] : iv_ruleOrExpression= ruleOrExpression EOF ;
    public final EObject entryRuleOrExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExpression = null;


        try {
            // InternalCTWedge.g:726:53: (iv_ruleOrExpression= ruleOrExpression EOF )
            // InternalCTWedge.g:727:2: iv_ruleOrExpression= ruleOrExpression EOF
            {
             newCompositeNode(grammarAccess.getOrExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOrExpression=ruleOrExpression();

            state._fsp--;

             current =iv_ruleOrExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOrExpression"


    // $ANTLR start "ruleOrExpression"
    // InternalCTWedge.g:733:1: ruleOrExpression returns [EObject current=null] : (this_AndExpression_0= ruleAndExpression ( () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) ) )* ) ;
    public final EObject ruleOrExpression() throws RecognitionException {
        EObject current = null;

        EObject this_AndExpression_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:739:2: ( (this_AndExpression_0= ruleAndExpression ( () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) ) )* ) )
            // InternalCTWedge.g:740:2: (this_AndExpression_0= ruleAndExpression ( () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) ) )* )
            {
            // InternalCTWedge.g:740:2: (this_AndExpression_0= ruleAndExpression ( () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) ) )* )
            // InternalCTWedge.g:741:3: this_AndExpression_0= ruleAndExpression ( () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) ) )*
            {

            			newCompositeNode(grammarAccess.getOrExpressionAccess().getAndExpressionParserRuleCall_0());
            		
            pushFollow(FOLLOW_31);
            this_AndExpression_0=ruleAndExpression();

            state._fsp--;


            			current = this_AndExpression_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:749:3: ( () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>=32 && LA15_0<=35)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // InternalCTWedge.g:750:4: () ruleOR_OPERATOR ( (lv_right_3_0= ruleAndExpression ) )
            	    {
            	    // InternalCTWedge.g:750:4: ()
            	    // InternalCTWedge.g:751:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getOrExpressionAccess().getOrExpressionLeftAction_1_0(),
            	    						current);
            	    				

            	    }


            	    				newCompositeNode(grammarAccess.getOrExpressionAccess().getOR_OPERATORParserRuleCall_1_1());
            	    			
            	    pushFollow(FOLLOW_29);
            	    ruleOR_OPERATOR();

            	    state._fsp--;


            	    				afterParserOrEnumRuleCall();
            	    			
            	    // InternalCTWedge.g:764:4: ( (lv_right_3_0= ruleAndExpression ) )
            	    // InternalCTWedge.g:765:5: (lv_right_3_0= ruleAndExpression )
            	    {
            	    // InternalCTWedge.g:765:5: (lv_right_3_0= ruleAndExpression )
            	    // InternalCTWedge.g:766:6: lv_right_3_0= ruleAndExpression
            	    {

            	    						newCompositeNode(grammarAccess.getOrExpressionAccess().getRightAndExpressionParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_31);
            	    lv_right_3_0=ruleAndExpression();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getOrExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.AndExpression");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOrExpression"


    // $ANTLR start "entryRuleAndExpression"
    // InternalCTWedge.g:788:1: entryRuleAndExpression returns [EObject current=null] : iv_ruleAndExpression= ruleAndExpression EOF ;
    public final EObject entryRuleAndExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExpression = null;


        try {
            // InternalCTWedge.g:788:54: (iv_ruleAndExpression= ruleAndExpression EOF )
            // InternalCTWedge.g:789:2: iv_ruleAndExpression= ruleAndExpression EOF
            {
             newCompositeNode(grammarAccess.getAndExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAndExpression=ruleAndExpression();

            state._fsp--;

             current =iv_ruleAndExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAndExpression"


    // $ANTLR start "ruleAndExpression"
    // InternalCTWedge.g:795:1: ruleAndExpression returns [EObject current=null] : (this_EqualExpression_0= ruleEqualExpression ( () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) ) )* ) ;
    public final EObject ruleAndExpression() throws RecognitionException {
        EObject current = null;

        EObject this_EqualExpression_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:801:2: ( (this_EqualExpression_0= ruleEqualExpression ( () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) ) )* ) )
            // InternalCTWedge.g:802:2: (this_EqualExpression_0= ruleEqualExpression ( () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) ) )* )
            {
            // InternalCTWedge.g:802:2: (this_EqualExpression_0= ruleEqualExpression ( () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) ) )* )
            // InternalCTWedge.g:803:3: this_EqualExpression_0= ruleEqualExpression ( () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) ) )*
            {

            			newCompositeNode(grammarAccess.getAndExpressionAccess().getEqualExpressionParserRuleCall_0());
            		
            pushFollow(FOLLOW_32);
            this_EqualExpression_0=ruleEqualExpression();

            state._fsp--;


            			current = this_EqualExpression_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:811:3: ( () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) ) )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>=36 && LA16_0<=39)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // InternalCTWedge.g:812:4: () ruleAND_OPERATOR ( (lv_right_3_0= ruleEqualExpression ) )
            	    {
            	    // InternalCTWedge.g:812:4: ()
            	    // InternalCTWedge.g:813:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getAndExpressionAccess().getAndExpressionLeftAction_1_0(),
            	    						current);
            	    				

            	    }


            	    				newCompositeNode(grammarAccess.getAndExpressionAccess().getAND_OPERATORParserRuleCall_1_1());
            	    			
            	    pushFollow(FOLLOW_29);
            	    ruleAND_OPERATOR();

            	    state._fsp--;


            	    				afterParserOrEnumRuleCall();
            	    			
            	    // InternalCTWedge.g:826:4: ( (lv_right_3_0= ruleEqualExpression ) )
            	    // InternalCTWedge.g:827:5: (lv_right_3_0= ruleEqualExpression )
            	    {
            	    // InternalCTWedge.g:827:5: (lv_right_3_0= ruleEqualExpression )
            	    // InternalCTWedge.g:828:6: lv_right_3_0= ruleEqualExpression
            	    {

            	    						newCompositeNode(grammarAccess.getAndExpressionAccess().getRightEqualExpressionParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_32);
            	    lv_right_3_0=ruleEqualExpression();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getAndExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.EqualExpression");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAndExpression"


    // $ANTLR start "entryRuleEqualExpression"
    // InternalCTWedge.g:850:1: entryRuleEqualExpression returns [EObject current=null] : iv_ruleEqualExpression= ruleEqualExpression EOF ;
    public final EObject entryRuleEqualExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEqualExpression = null;


        try {
            // InternalCTWedge.g:850:56: (iv_ruleEqualExpression= ruleEqualExpression EOF )
            // InternalCTWedge.g:851:2: iv_ruleEqualExpression= ruleEqualExpression EOF
            {
             newCompositeNode(grammarAccess.getEqualExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleEqualExpression=ruleEqualExpression();

            state._fsp--;

             current =iv_ruleEqualExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEqualExpression"


    // $ANTLR start "ruleEqualExpression"
    // InternalCTWedge.g:857:1: ruleEqualExpression returns [EObject current=null] : (this_RelationalExpression_0= ruleRelationalExpression ( () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) ) )* ) ;
    public final EObject ruleEqualExpression() throws RecognitionException {
        EObject current = null;

        EObject this_RelationalExpression_0 = null;

        Enumerator lv_op_2_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:863:2: ( (this_RelationalExpression_0= ruleRelationalExpression ( () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) ) )* ) )
            // InternalCTWedge.g:864:2: (this_RelationalExpression_0= ruleRelationalExpression ( () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) ) )* )
            {
            // InternalCTWedge.g:864:2: (this_RelationalExpression_0= ruleRelationalExpression ( () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) ) )* )
            // InternalCTWedge.g:865:3: this_RelationalExpression_0= ruleRelationalExpression ( () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) ) )*
            {

            			newCompositeNode(grammarAccess.getEqualExpressionAccess().getRelationalExpressionParserRuleCall_0());
            		
            pushFollow(FOLLOW_33);
            this_RelationalExpression_0=ruleRelationalExpression();

            state._fsp--;


            			current = this_RelationalExpression_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:873:3: ( () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>=48 && LA17_0<=50)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // InternalCTWedge.g:874:4: () ( (lv_op_2_0= ruleEqualityOperators ) ) ( (lv_right_3_0= ruleRelationalExpression ) )
            	    {
            	    // InternalCTWedge.g:874:4: ()
            	    // InternalCTWedge.g:875:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getEqualExpressionAccess().getEqualExpressionLeftAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalCTWedge.g:881:4: ( (lv_op_2_0= ruleEqualityOperators ) )
            	    // InternalCTWedge.g:882:5: (lv_op_2_0= ruleEqualityOperators )
            	    {
            	    // InternalCTWedge.g:882:5: (lv_op_2_0= ruleEqualityOperators )
            	    // InternalCTWedge.g:883:6: lv_op_2_0= ruleEqualityOperators
            	    {

            	    						newCompositeNode(grammarAccess.getEqualExpressionAccess().getOpEqualityOperatorsEnumRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_29);
            	    lv_op_2_0=ruleEqualityOperators();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getEqualExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"op",
            	    							lv_op_2_0,
            	    							"ctwedge.CTWedge.EqualityOperators");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }

            	    // InternalCTWedge.g:900:4: ( (lv_right_3_0= ruleRelationalExpression ) )
            	    // InternalCTWedge.g:901:5: (lv_right_3_0= ruleRelationalExpression )
            	    {
            	    // InternalCTWedge.g:901:5: (lv_right_3_0= ruleRelationalExpression )
            	    // InternalCTWedge.g:902:6: lv_right_3_0= ruleRelationalExpression
            	    {

            	    						newCompositeNode(grammarAccess.getEqualExpressionAccess().getRightRelationalExpressionParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_33);
            	    lv_right_3_0=ruleRelationalExpression();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getEqualExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.RelationalExpression");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEqualExpression"


    // $ANTLR start "entryRuleRelationalExpression"
    // InternalCTWedge.g:924:1: entryRuleRelationalExpression returns [EObject current=null] : iv_ruleRelationalExpression= ruleRelationalExpression EOF ;
    public final EObject entryRuleRelationalExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelationalExpression = null;


        try {
            // InternalCTWedge.g:924:61: (iv_ruleRelationalExpression= ruleRelationalExpression EOF )
            // InternalCTWedge.g:925:2: iv_ruleRelationalExpression= ruleRelationalExpression EOF
            {
             newCompositeNode(grammarAccess.getRelationalExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleRelationalExpression=ruleRelationalExpression();

            state._fsp--;

             current =iv_ruleRelationalExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelationalExpression"


    // $ANTLR start "ruleRelationalExpression"
    // InternalCTWedge.g:931:1: ruleRelationalExpression returns [EObject current=null] : (this_PlusMinus_0= rulePlusMinus ( () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) ) )* ) ;
    public final EObject ruleRelationalExpression() throws RecognitionException {
        EObject current = null;

        EObject this_PlusMinus_0 = null;

        Enumerator lv_op_2_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:937:2: ( (this_PlusMinus_0= rulePlusMinus ( () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) ) )* ) )
            // InternalCTWedge.g:938:2: (this_PlusMinus_0= rulePlusMinus ( () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) ) )* )
            {
            // InternalCTWedge.g:938:2: (this_PlusMinus_0= rulePlusMinus ( () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) ) )* )
            // InternalCTWedge.g:939:3: this_PlusMinus_0= rulePlusMinus ( () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) ) )*
            {

            			newCompositeNode(grammarAccess.getRelationalExpressionAccess().getPlusMinusParserRuleCall_0());
            		
            pushFollow(FOLLOW_34);
            this_PlusMinus_0=rulePlusMinus();

            state._fsp--;


            			current = this_PlusMinus_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:947:3: ( () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( ((LA18_0>=44 && LA18_0<=47)) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // InternalCTWedge.g:948:4: () ( (lv_op_2_0= ruleRelationalOperators ) ) ( (lv_right_3_0= rulePlusMinus ) )
            	    {
            	    // InternalCTWedge.g:948:4: ()
            	    // InternalCTWedge.g:949:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getRelationalExpressionAccess().getRelationalExpressionLeftAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalCTWedge.g:955:4: ( (lv_op_2_0= ruleRelationalOperators ) )
            	    // InternalCTWedge.g:956:5: (lv_op_2_0= ruleRelationalOperators )
            	    {
            	    // InternalCTWedge.g:956:5: (lv_op_2_0= ruleRelationalOperators )
            	    // InternalCTWedge.g:957:6: lv_op_2_0= ruleRelationalOperators
            	    {

            	    						newCompositeNode(grammarAccess.getRelationalExpressionAccess().getOpRelationalOperatorsEnumRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_29);
            	    lv_op_2_0=ruleRelationalOperators();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getRelationalExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"op",
            	    							lv_op_2_0,
            	    							"ctwedge.CTWedge.RelationalOperators");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }

            	    // InternalCTWedge.g:974:4: ( (lv_right_3_0= rulePlusMinus ) )
            	    // InternalCTWedge.g:975:5: (lv_right_3_0= rulePlusMinus )
            	    {
            	    // InternalCTWedge.g:975:5: (lv_right_3_0= rulePlusMinus )
            	    // InternalCTWedge.g:976:6: lv_right_3_0= rulePlusMinus
            	    {

            	    						newCompositeNode(grammarAccess.getRelationalExpressionAccess().getRightPlusMinusParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_34);
            	    lv_right_3_0=rulePlusMinus();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getRelationalExpressionRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.PlusMinus");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationalExpression"


    // $ANTLR start "entryRulePlusMinus"
    // InternalCTWedge.g:998:1: entryRulePlusMinus returns [EObject current=null] : iv_rulePlusMinus= rulePlusMinus EOF ;
    public final EObject entryRulePlusMinus() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePlusMinus = null;


        try {
            // InternalCTWedge.g:998:50: (iv_rulePlusMinus= rulePlusMinus EOF )
            // InternalCTWedge.g:999:2: iv_rulePlusMinus= rulePlusMinus EOF
            {
             newCompositeNode(grammarAccess.getPlusMinusRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePlusMinus=rulePlusMinus();

            state._fsp--;

             current =iv_rulePlusMinus; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePlusMinus"


    // $ANTLR start "rulePlusMinus"
    // InternalCTWedge.g:1005:1: rulePlusMinus returns [EObject current=null] : (this_ModMultDiv_0= ruleModMultDiv ( () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) ) )* ) ;
    public final EObject rulePlusMinus() throws RecognitionException {
        EObject current = null;

        EObject this_ModMultDiv_0 = null;

        Enumerator lv_op_2_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:1011:2: ( (this_ModMultDiv_0= ruleModMultDiv ( () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) ) )* ) )
            // InternalCTWedge.g:1012:2: (this_ModMultDiv_0= ruleModMultDiv ( () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) ) )* )
            {
            // InternalCTWedge.g:1012:2: (this_ModMultDiv_0= ruleModMultDiv ( () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) ) )* )
            // InternalCTWedge.g:1013:3: this_ModMultDiv_0= ruleModMultDiv ( () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) ) )*
            {

            			newCompositeNode(grammarAccess.getPlusMinusAccess().getModMultDivParserRuleCall_0());
            		
            pushFollow(FOLLOW_35);
            this_ModMultDiv_0=ruleModMultDiv();

            state._fsp--;


            			current = this_ModMultDiv_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:1021:3: ( () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==43||LA19_0==51) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // InternalCTWedge.g:1022:4: () ( (lv_op_2_0= rulePlusMinusOperators ) ) ( (lv_right_3_0= ruleModMultDiv ) )
            	    {
            	    // InternalCTWedge.g:1022:4: ()
            	    // InternalCTWedge.g:1023:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getPlusMinusAccess().getPlusMinusLeftAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalCTWedge.g:1029:4: ( (lv_op_2_0= rulePlusMinusOperators ) )
            	    // InternalCTWedge.g:1030:5: (lv_op_2_0= rulePlusMinusOperators )
            	    {
            	    // InternalCTWedge.g:1030:5: (lv_op_2_0= rulePlusMinusOperators )
            	    // InternalCTWedge.g:1031:6: lv_op_2_0= rulePlusMinusOperators
            	    {

            	    						newCompositeNode(grammarAccess.getPlusMinusAccess().getOpPlusMinusOperatorsEnumRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_29);
            	    lv_op_2_0=rulePlusMinusOperators();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getPlusMinusRule());
            	    						}
            	    						set(
            	    							current,
            	    							"op",
            	    							lv_op_2_0,
            	    							"ctwedge.CTWedge.PlusMinusOperators");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }

            	    // InternalCTWedge.g:1048:4: ( (lv_right_3_0= ruleModMultDiv ) )
            	    // InternalCTWedge.g:1049:5: (lv_right_3_0= ruleModMultDiv )
            	    {
            	    // InternalCTWedge.g:1049:5: (lv_right_3_0= ruleModMultDiv )
            	    // InternalCTWedge.g:1050:6: lv_right_3_0= ruleModMultDiv
            	    {

            	    						newCompositeNode(grammarAccess.getPlusMinusAccess().getRightModMultDivParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_35);
            	    lv_right_3_0=ruleModMultDiv();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getPlusMinusRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.ModMultDiv");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePlusMinus"


    // $ANTLR start "entryRuleModMultDiv"
    // InternalCTWedge.g:1072:1: entryRuleModMultDiv returns [EObject current=null] : iv_ruleModMultDiv= ruleModMultDiv EOF ;
    public final EObject entryRuleModMultDiv() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModMultDiv = null;


        try {
            // InternalCTWedge.g:1072:51: (iv_ruleModMultDiv= ruleModMultDiv EOF )
            // InternalCTWedge.g:1073:2: iv_ruleModMultDiv= ruleModMultDiv EOF
            {
             newCompositeNode(grammarAccess.getModMultDivRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleModMultDiv=ruleModMultDiv();

            state._fsp--;

             current =iv_ruleModMultDiv; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModMultDiv"


    // $ANTLR start "ruleModMultDiv"
    // InternalCTWedge.g:1079:1: ruleModMultDiv returns [EObject current=null] : (this_Primary_0= rulePrimary ( () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) ) )* ) ;
    public final EObject ruleModMultDiv() throws RecognitionException {
        EObject current = null;

        EObject this_Primary_0 = null;

        Enumerator lv_op_2_0 = null;

        EObject lv_right_3_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:1085:2: ( (this_Primary_0= rulePrimary ( () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) ) )* ) )
            // InternalCTWedge.g:1086:2: (this_Primary_0= rulePrimary ( () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) ) )* )
            {
            // InternalCTWedge.g:1086:2: (this_Primary_0= rulePrimary ( () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) ) )* )
            // InternalCTWedge.g:1087:3: this_Primary_0= rulePrimary ( () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) ) )*
            {

            			newCompositeNode(grammarAccess.getModMultDivAccess().getPrimaryParserRuleCall_0());
            		
            pushFollow(FOLLOW_36);
            this_Primary_0=rulePrimary();

            state._fsp--;


            			current = this_Primary_0;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:1095:3: ( () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>=52 && LA20_0<=54)) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // InternalCTWedge.g:1096:4: () ( (lv_op_2_0= ruleModMultDivOperators ) ) ( (lv_right_3_0= rulePrimary ) )
            	    {
            	    // InternalCTWedge.g:1096:4: ()
            	    // InternalCTWedge.g:1097:5: 
            	    {

            	    					current = forceCreateModelElementAndSet(
            	    						grammarAccess.getModMultDivAccess().getModMultDivLeftAction_1_0(),
            	    						current);
            	    				

            	    }

            	    // InternalCTWedge.g:1103:4: ( (lv_op_2_0= ruleModMultDivOperators ) )
            	    // InternalCTWedge.g:1104:5: (lv_op_2_0= ruleModMultDivOperators )
            	    {
            	    // InternalCTWedge.g:1104:5: (lv_op_2_0= ruleModMultDivOperators )
            	    // InternalCTWedge.g:1105:6: lv_op_2_0= ruleModMultDivOperators
            	    {

            	    						newCompositeNode(grammarAccess.getModMultDivAccess().getOpModMultDivOperatorsEnumRuleCall_1_1_0());
            	    					
            	    pushFollow(FOLLOW_29);
            	    lv_op_2_0=ruleModMultDivOperators();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getModMultDivRule());
            	    						}
            	    						set(
            	    							current,
            	    							"op",
            	    							lv_op_2_0,
            	    							"ctwedge.CTWedge.ModMultDivOperators");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }

            	    // InternalCTWedge.g:1122:4: ( (lv_right_3_0= rulePrimary ) )
            	    // InternalCTWedge.g:1123:5: (lv_right_3_0= rulePrimary )
            	    {
            	    // InternalCTWedge.g:1123:5: (lv_right_3_0= rulePrimary )
            	    // InternalCTWedge.g:1124:6: lv_right_3_0= rulePrimary
            	    {

            	    						newCompositeNode(grammarAccess.getModMultDivAccess().getRightPrimaryParserRuleCall_1_2_0());
            	    					
            	    pushFollow(FOLLOW_36);
            	    lv_right_3_0=rulePrimary();

            	    state._fsp--;


            	    						if (current==null) {
            	    							current = createModelElementForParent(grammarAccess.getModMultDivRule());
            	    						}
            	    						set(
            	    							current,
            	    							"right",
            	    							lv_right_3_0,
            	    							"ctwedge.CTWedge.Primary");
            	    						afterParserOrEnumRuleCall();
            	    					

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModMultDiv"


    // $ANTLR start "entryRulePrimary"
    // InternalCTWedge.g:1146:1: entryRulePrimary returns [EObject current=null] : iv_rulePrimary= rulePrimary EOF ;
    public final EObject entryRulePrimary() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimary = null;


        try {
            // InternalCTWedge.g:1146:48: (iv_rulePrimary= rulePrimary EOF )
            // InternalCTWedge.g:1147:2: iv_rulePrimary= rulePrimary EOF
            {
             newCompositeNode(grammarAccess.getPrimaryRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePrimary=rulePrimary();

            state._fsp--;

             current =iv_rulePrimary; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePrimary"


    // $ANTLR start "rulePrimary"
    // InternalCTWedge.g:1153:1: rulePrimary returns [EObject current=null] : (this_NotExpression_0= ruleNotExpression | (otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')' ) | this_AtomicPredicate_4= ruleAtomicPredicate ) ;
    public final EObject rulePrimary() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject this_NotExpression_0 = null;

        EObject this_ImpliesExpression_2 = null;

        EObject this_AtomicPredicate_4 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:1159:2: ( (this_NotExpression_0= ruleNotExpression | (otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')' ) | this_AtomicPredicate_4= ruleAtomicPredicate ) )
            // InternalCTWedge.g:1160:2: (this_NotExpression_0= ruleNotExpression | (otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')' ) | this_AtomicPredicate_4= ruleAtomicPredicate )
            {
            // InternalCTWedge.g:1160:2: (this_NotExpression_0= ruleNotExpression | (otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')' ) | this_AtomicPredicate_4= ruleAtomicPredicate )
            int alt21=3;
            switch ( input.LA(1) ) {
            case 40:
            case 41:
            case 42:
                {
                alt21=1;
                }
                break;
            case 30:
                {
                alt21=2;
                }
                break;
            case RULE_ID:
            case RULE_INT:
            case RULE_NUMID:
            case RULE_STRING:
            case 19:
            case 21:
            case 23:
            case 24:
            case 43:
                {
                alt21=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // InternalCTWedge.g:1161:3: this_NotExpression_0= ruleNotExpression
                    {

                    			newCompositeNode(grammarAccess.getPrimaryAccess().getNotExpressionParserRuleCall_0());
                    		
                    pushFollow(FOLLOW_2);
                    this_NotExpression_0=ruleNotExpression();

                    state._fsp--;


                    			current = this_NotExpression_0;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1170:3: (otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')' )
                    {
                    // InternalCTWedge.g:1170:3: (otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')' )
                    // InternalCTWedge.g:1171:4: otherlv_1= '(' this_ImpliesExpression_2= ruleImpliesExpression otherlv_3= ')'
                    {
                    otherlv_1=(Token)match(input,30,FOLLOW_29); 

                    				newLeafNode(otherlv_1, grammarAccess.getPrimaryAccess().getLeftParenthesisKeyword_1_0());
                    			

                    				newCompositeNode(grammarAccess.getPrimaryAccess().getImpliesExpressionParserRuleCall_1_1());
                    			
                    pushFollow(FOLLOW_37);
                    this_ImpliesExpression_2=ruleImpliesExpression();

                    state._fsp--;


                    				current = this_ImpliesExpression_2;
                    				afterParserOrEnumRuleCall();
                    			
                    otherlv_3=(Token)match(input,31,FOLLOW_2); 

                    				newLeafNode(otherlv_3, grammarAccess.getPrimaryAccess().getRightParenthesisKeyword_1_2());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1189:3: this_AtomicPredicate_4= ruleAtomicPredicate
                    {

                    			newCompositeNode(grammarAccess.getPrimaryAccess().getAtomicPredicateParserRuleCall_2());
                    		
                    pushFollow(FOLLOW_2);
                    this_AtomicPredicate_4=ruleAtomicPredicate();

                    state._fsp--;


                    			current = this_AtomicPredicate_4;
                    			afterParserOrEnumRuleCall();
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePrimary"


    // $ANTLR start "entryRuleNotExpression"
    // InternalCTWedge.g:1201:1: entryRuleNotExpression returns [EObject current=null] : iv_ruleNotExpression= ruleNotExpression EOF ;
    public final EObject entryRuleNotExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNotExpression = null;


        try {
            // InternalCTWedge.g:1201:54: (iv_ruleNotExpression= ruleNotExpression EOF )
            // InternalCTWedge.g:1202:2: iv_ruleNotExpression= ruleNotExpression EOF
            {
             newCompositeNode(grammarAccess.getNotExpressionRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleNotExpression=ruleNotExpression();

            state._fsp--;

             current =iv_ruleNotExpression; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNotExpression"


    // $ANTLR start "ruleNotExpression"
    // InternalCTWedge.g:1208:1: ruleNotExpression returns [EObject current=null] : ( ruleNOT_OPERATOR this_Primary_1= rulePrimary () ) ;
    public final EObject ruleNotExpression() throws RecognitionException {
        EObject current = null;

        EObject this_Primary_1 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:1214:2: ( ( ruleNOT_OPERATOR this_Primary_1= rulePrimary () ) )
            // InternalCTWedge.g:1215:2: ( ruleNOT_OPERATOR this_Primary_1= rulePrimary () )
            {
            // InternalCTWedge.g:1215:2: ( ruleNOT_OPERATOR this_Primary_1= rulePrimary () )
            // InternalCTWedge.g:1216:3: ruleNOT_OPERATOR this_Primary_1= rulePrimary ()
            {

            			newCompositeNode(grammarAccess.getNotExpressionAccess().getNOT_OPERATORParserRuleCall_0());
            		
            pushFollow(FOLLOW_29);
            ruleNOT_OPERATOR();

            state._fsp--;


            			afterParserOrEnumRuleCall();
            		

            			newCompositeNode(grammarAccess.getNotExpressionAccess().getPrimaryParserRuleCall_1());
            		
            pushFollow(FOLLOW_2);
            this_Primary_1=rulePrimary();

            state._fsp--;


            			current = this_Primary_1;
            			afterParserOrEnumRuleCall();
            		
            // InternalCTWedge.g:1231:3: ()
            // InternalCTWedge.g:1232:4: 
            {

            				current = forceCreateModelElementAndSet(
            					grammarAccess.getNotExpressionAccess().getNotExpressionPredicateAction_2(),
            					current);
            			

            }


            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNotExpression"


    // $ANTLR start "entryRuleAtomicPredicate"
    // InternalCTWedge.g:1242:1: entryRuleAtomicPredicate returns [EObject current=null] : iv_ruleAtomicPredicate= ruleAtomicPredicate EOF ;
    public final EObject entryRuleAtomicPredicate() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAtomicPredicate = null;


        try {
            // InternalCTWedge.g:1242:56: (iv_ruleAtomicPredicate= ruleAtomicPredicate EOF )
            // InternalCTWedge.g:1243:2: iv_ruleAtomicPredicate= ruleAtomicPredicate EOF
            {
             newCompositeNode(grammarAccess.getAtomicPredicateRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAtomicPredicate=ruleAtomicPredicate();

            state._fsp--;

             current =iv_ruleAtomicPredicate; 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAtomicPredicate"


    // $ANTLR start "ruleAtomicPredicate"
    // InternalCTWedge.g:1249:1: ruleAtomicPredicate returns [EObject current=null] : ( ( (lv_boolConst_0_0= ruleBoolConst ) ) | ( (lv_name_1_0= ruleelementID ) ) ) ;
    public final EObject ruleAtomicPredicate() throws RecognitionException {
        EObject current = null;

        AntlrDatatypeRuleToken lv_boolConst_0_0 = null;

        AntlrDatatypeRuleToken lv_name_1_0 = null;



        	enterRule();

        try {
            // InternalCTWedge.g:1255:2: ( ( ( (lv_boolConst_0_0= ruleBoolConst ) ) | ( (lv_name_1_0= ruleelementID ) ) ) )
            // InternalCTWedge.g:1256:2: ( ( (lv_boolConst_0_0= ruleBoolConst ) ) | ( (lv_name_1_0= ruleelementID ) ) )
            {
            // InternalCTWedge.g:1256:2: ( ( (lv_boolConst_0_0= ruleBoolConst ) ) | ( (lv_name_1_0= ruleelementID ) ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==19||LA22_0==21||(LA22_0>=23 && LA22_0<=24)) ) {
                alt22=1;
            }
            else if ( ((LA22_0>=RULE_ID && LA22_0<=RULE_STRING)||LA22_0==43) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // InternalCTWedge.g:1257:3: ( (lv_boolConst_0_0= ruleBoolConst ) )
                    {
                    // InternalCTWedge.g:1257:3: ( (lv_boolConst_0_0= ruleBoolConst ) )
                    // InternalCTWedge.g:1258:4: (lv_boolConst_0_0= ruleBoolConst )
                    {
                    // InternalCTWedge.g:1258:4: (lv_boolConst_0_0= ruleBoolConst )
                    // InternalCTWedge.g:1259:5: lv_boolConst_0_0= ruleBoolConst
                    {

                    					newCompositeNode(grammarAccess.getAtomicPredicateAccess().getBoolConstBoolConstParserRuleCall_0_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_boolConst_0_0=ruleBoolConst();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getAtomicPredicateRule());
                    					}
                    					set(
                    						current,
                    						"boolConst",
                    						lv_boolConst_0_0,
                    						"ctwedge.CTWedge.BoolConst");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }


                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1277:3: ( (lv_name_1_0= ruleelementID ) )
                    {
                    // InternalCTWedge.g:1277:3: ( (lv_name_1_0= ruleelementID ) )
                    // InternalCTWedge.g:1278:4: (lv_name_1_0= ruleelementID )
                    {
                    // InternalCTWedge.g:1278:4: (lv_name_1_0= ruleelementID )
                    // InternalCTWedge.g:1279:5: lv_name_1_0= ruleelementID
                    {

                    					newCompositeNode(grammarAccess.getAtomicPredicateAccess().getNameElementIDParserRuleCall_1_0());
                    				
                    pushFollow(FOLLOW_2);
                    lv_name_1_0=ruleelementID();

                    state._fsp--;


                    					if (current==null) {
                    						current = createModelElementForParent(grammarAccess.getAtomicPredicateRule());
                    					}
                    					set(
                    						current,
                    						"name",
                    						lv_name_1_0,
                    						"ctwedge.CTWedge.elementID");
                    					afterParserOrEnumRuleCall();
                    				

                    }


                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAtomicPredicate"


    // $ANTLR start "entryRuleOR_OPERATOR"
    // InternalCTWedge.g:1300:1: entryRuleOR_OPERATOR returns [String current=null] : iv_ruleOR_OPERATOR= ruleOR_OPERATOR EOF ;
    public final String entryRuleOR_OPERATOR() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleOR_OPERATOR = null;


        try {
            // InternalCTWedge.g:1300:51: (iv_ruleOR_OPERATOR= ruleOR_OPERATOR EOF )
            // InternalCTWedge.g:1301:2: iv_ruleOR_OPERATOR= ruleOR_OPERATOR EOF
            {
             newCompositeNode(grammarAccess.getOR_OPERATORRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleOR_OPERATOR=ruleOR_OPERATOR();

            state._fsp--;

             current =iv_ruleOR_OPERATOR.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOR_OPERATOR"


    // $ANTLR start "ruleOR_OPERATOR"
    // InternalCTWedge.g:1307:1: ruleOR_OPERATOR returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= '||' | kw= 'or' | kw= 'OR' | kw= '|' ) ;
    public final AntlrDatatypeRuleToken ruleOR_OPERATOR() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1313:2: ( (kw= '||' | kw= 'or' | kw= 'OR' | kw= '|' ) )
            // InternalCTWedge.g:1314:2: (kw= '||' | kw= 'or' | kw= 'OR' | kw= '|' )
            {
            // InternalCTWedge.g:1314:2: (kw= '||' | kw= 'or' | kw= 'OR' | kw= '|' )
            int alt23=4;
            switch ( input.LA(1) ) {
            case 32:
                {
                alt23=1;
                }
                break;
            case 33:
                {
                alt23=2;
                }
                break;
            case 34:
                {
                alt23=3;
                }
                break;
            case 35:
                {
                alt23=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // InternalCTWedge.g:1315:3: kw= '||'
                    {
                    kw=(Token)match(input,32,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getOR_OPERATORAccess().getVerticalLineVerticalLineKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1321:3: kw= 'or'
                    {
                    kw=(Token)match(input,33,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getOR_OPERATORAccess().getOrKeyword_1());
                    		

                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1327:3: kw= 'OR'
                    {
                    kw=(Token)match(input,34,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getOR_OPERATORAccess().getORKeyword_2());
                    		

                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:1333:3: kw= '|'
                    {
                    kw=(Token)match(input,35,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getOR_OPERATORAccess().getVerticalLineKeyword_3());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOR_OPERATOR"


    // $ANTLR start "entryRuleAND_OPERATOR"
    // InternalCTWedge.g:1342:1: entryRuleAND_OPERATOR returns [String current=null] : iv_ruleAND_OPERATOR= ruleAND_OPERATOR EOF ;
    public final String entryRuleAND_OPERATOR() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleAND_OPERATOR = null;


        try {
            // InternalCTWedge.g:1342:52: (iv_ruleAND_OPERATOR= ruleAND_OPERATOR EOF )
            // InternalCTWedge.g:1343:2: iv_ruleAND_OPERATOR= ruleAND_OPERATOR EOF
            {
             newCompositeNode(grammarAccess.getAND_OPERATORRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleAND_OPERATOR=ruleAND_OPERATOR();

            state._fsp--;

             current =iv_ruleAND_OPERATOR.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAND_OPERATOR"


    // $ANTLR start "ruleAND_OPERATOR"
    // InternalCTWedge.g:1349:1: ruleAND_OPERATOR returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= '&&' | kw= 'and' | kw= 'AND' | kw= '&' ) ;
    public final AntlrDatatypeRuleToken ruleAND_OPERATOR() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1355:2: ( (kw= '&&' | kw= 'and' | kw= 'AND' | kw= '&' ) )
            // InternalCTWedge.g:1356:2: (kw= '&&' | kw= 'and' | kw= 'AND' | kw= '&' )
            {
            // InternalCTWedge.g:1356:2: (kw= '&&' | kw= 'and' | kw= 'AND' | kw= '&' )
            int alt24=4;
            switch ( input.LA(1) ) {
            case 36:
                {
                alt24=1;
                }
                break;
            case 37:
                {
                alt24=2;
                }
                break;
            case 38:
                {
                alt24=3;
                }
                break;
            case 39:
                {
                alt24=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // InternalCTWedge.g:1357:3: kw= '&&'
                    {
                    kw=(Token)match(input,36,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getAND_OPERATORAccess().getAmpersandAmpersandKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1363:3: kw= 'and'
                    {
                    kw=(Token)match(input,37,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getAND_OPERATORAccess().getAndKeyword_1());
                    		

                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1369:3: kw= 'AND'
                    {
                    kw=(Token)match(input,38,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getAND_OPERATORAccess().getANDKeyword_2());
                    		

                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:1375:3: kw= '&'
                    {
                    kw=(Token)match(input,39,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getAND_OPERATORAccess().getAmpersandKeyword_3());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAND_OPERATOR"


    // $ANTLR start "entryRuleNOT_OPERATOR"
    // InternalCTWedge.g:1384:1: entryRuleNOT_OPERATOR returns [String current=null] : iv_ruleNOT_OPERATOR= ruleNOT_OPERATOR EOF ;
    public final String entryRuleNOT_OPERATOR() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleNOT_OPERATOR = null;


        try {
            // InternalCTWedge.g:1384:52: (iv_ruleNOT_OPERATOR= ruleNOT_OPERATOR EOF )
            // InternalCTWedge.g:1385:2: iv_ruleNOT_OPERATOR= ruleNOT_OPERATOR EOF
            {
             newCompositeNode(grammarAccess.getNOT_OPERATORRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleNOT_OPERATOR=ruleNOT_OPERATOR();

            state._fsp--;

             current =iv_ruleNOT_OPERATOR.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleNOT_OPERATOR"


    // $ANTLR start "ruleNOT_OPERATOR"
    // InternalCTWedge.g:1391:1: ruleNOT_OPERATOR returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= '!' | kw= 'not' | kw= 'NOT' ) ;
    public final AntlrDatatypeRuleToken ruleNOT_OPERATOR() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1397:2: ( (kw= '!' | kw= 'not' | kw= 'NOT' ) )
            // InternalCTWedge.g:1398:2: (kw= '!' | kw= 'not' | kw= 'NOT' )
            {
            // InternalCTWedge.g:1398:2: (kw= '!' | kw= 'not' | kw= 'NOT' )
            int alt25=3;
            switch ( input.LA(1) ) {
            case 40:
                {
                alt25=1;
                }
                break;
            case 41:
                {
                alt25=2;
                }
                break;
            case 42:
                {
                alt25=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // InternalCTWedge.g:1399:3: kw= '!'
                    {
                    kw=(Token)match(input,40,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getNOT_OPERATORAccess().getExclamationMarkKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1405:3: kw= 'not'
                    {
                    kw=(Token)match(input,41,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getNOT_OPERATORAccess().getNotKeyword_1());
                    		

                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1411:3: kw= 'NOT'
                    {
                    kw=(Token)match(input,42,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getNOT_OPERATORAccess().getNOTKeyword_2());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleNOT_OPERATOR"


    // $ANTLR start "entryRuleBoolConst"
    // InternalCTWedge.g:1420:1: entryRuleBoolConst returns [String current=null] : iv_ruleBoolConst= ruleBoolConst EOF ;
    public final String entryRuleBoolConst() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBoolConst = null;


        try {
            // InternalCTWedge.g:1420:49: (iv_ruleBoolConst= ruleBoolConst EOF )
            // InternalCTWedge.g:1421:2: iv_ruleBoolConst= ruleBoolConst EOF
            {
             newCompositeNode(grammarAccess.getBoolConstRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleBoolConst=ruleBoolConst();

            state._fsp--;

             current =iv_ruleBoolConst.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBoolConst"


    // $ANTLR start "ruleBoolConst"
    // InternalCTWedge.g:1427:1: ruleBoolConst returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'false' | kw= 'true' | kw= 'FALSE' | kw= 'TRUE' ) ;
    public final AntlrDatatypeRuleToken ruleBoolConst() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1433:2: ( (kw= 'false' | kw= 'true' | kw= 'FALSE' | kw= 'TRUE' ) )
            // InternalCTWedge.g:1434:2: (kw= 'false' | kw= 'true' | kw= 'FALSE' | kw= 'TRUE' )
            {
            // InternalCTWedge.g:1434:2: (kw= 'false' | kw= 'true' | kw= 'FALSE' | kw= 'TRUE' )
            int alt26=4;
            switch ( input.LA(1) ) {
            case 24:
                {
                alt26=1;
                }
                break;
            case 23:
                {
                alt26=2;
                }
                break;
            case 21:
                {
                alt26=3;
                }
                break;
            case 19:
                {
                alt26=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // InternalCTWedge.g:1435:3: kw= 'false'
                    {
                    kw=(Token)match(input,24,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBoolConstAccess().getFalseKeyword_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1441:3: kw= 'true'
                    {
                    kw=(Token)match(input,23,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBoolConstAccess().getTrueKeyword_1());
                    		

                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1447:3: kw= 'FALSE'
                    {
                    kw=(Token)match(input,21,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBoolConstAccess().getFALSEKeyword_2());
                    		

                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:1453:3: kw= 'TRUE'
                    {
                    kw=(Token)match(input,19,FOLLOW_2); 

                    			current.merge(kw);
                    			newLeafNode(kw, grammarAccess.getBoolConstAccess().getTRUEKeyword_3());
                    		

                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBoolConst"


    // $ANTLR start "entryRuleelementID"
    // InternalCTWedge.g:1462:1: entryRuleelementID returns [String current=null] : iv_ruleelementID= ruleelementID EOF ;
    public final String entryRuleelementID() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleelementID = null;


        try {
            // InternalCTWedge.g:1462:49: (iv_ruleelementID= ruleelementID EOF )
            // InternalCTWedge.g:1463:2: iv_ruleelementID= ruleelementID EOF
            {
             newCompositeNode(grammarAccess.getElementIDRule()); 
            pushFollow(FOLLOW_1);
            iv_ruleelementID=ruleelementID();

            state._fsp--;

             current =iv_ruleelementID.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleelementID"


    // $ANTLR start "ruleelementID"
    // InternalCTWedge.g:1469:1: ruleelementID returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (this_ID_0= RULE_ID | this_NUMID_1= RULE_NUMID | this_STRING_2= RULE_STRING | ( (kw= '-' )? this_INT_4= RULE_INT ) ) ;
    public final AntlrDatatypeRuleToken ruleelementID() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token this_ID_0=null;
        Token this_NUMID_1=null;
        Token this_STRING_2=null;
        Token kw=null;
        Token this_INT_4=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1475:2: ( (this_ID_0= RULE_ID | this_NUMID_1= RULE_NUMID | this_STRING_2= RULE_STRING | ( (kw= '-' )? this_INT_4= RULE_INT ) ) )
            // InternalCTWedge.g:1476:2: (this_ID_0= RULE_ID | this_NUMID_1= RULE_NUMID | this_STRING_2= RULE_STRING | ( (kw= '-' )? this_INT_4= RULE_INT ) )
            {
            // InternalCTWedge.g:1476:2: (this_ID_0= RULE_ID | this_NUMID_1= RULE_NUMID | this_STRING_2= RULE_STRING | ( (kw= '-' )? this_INT_4= RULE_INT ) )
            int alt28=4;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt28=1;
                }
                break;
            case RULE_NUMID:
                {
                alt28=2;
                }
                break;
            case RULE_STRING:
                {
                alt28=3;
                }
                break;
            case RULE_INT:
            case 43:
                {
                alt28=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // InternalCTWedge.g:1477:3: this_ID_0= RULE_ID
                    {
                    this_ID_0=(Token)match(input,RULE_ID,FOLLOW_2); 

                    			current.merge(this_ID_0);
                    		

                    			newLeafNode(this_ID_0, grammarAccess.getElementIDAccess().getIDTerminalRuleCall_0());
                    		

                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1485:3: this_NUMID_1= RULE_NUMID
                    {
                    this_NUMID_1=(Token)match(input,RULE_NUMID,FOLLOW_2); 

                    			current.merge(this_NUMID_1);
                    		

                    			newLeafNode(this_NUMID_1, grammarAccess.getElementIDAccess().getNUMIDTerminalRuleCall_1());
                    		

                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1493:3: this_STRING_2= RULE_STRING
                    {
                    this_STRING_2=(Token)match(input,RULE_STRING,FOLLOW_2); 

                    			current.merge(this_STRING_2);
                    		

                    			newLeafNode(this_STRING_2, grammarAccess.getElementIDAccess().getSTRINGTerminalRuleCall_2());
                    		

                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:1501:3: ( (kw= '-' )? this_INT_4= RULE_INT )
                    {
                    // InternalCTWedge.g:1501:3: ( (kw= '-' )? this_INT_4= RULE_INT )
                    // InternalCTWedge.g:1502:4: (kw= '-' )? this_INT_4= RULE_INT
                    {
                    // InternalCTWedge.g:1502:4: (kw= '-' )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==43) ) {
                        alt27=1;
                    }
                    switch (alt27) {
                        case 1 :
                            // InternalCTWedge.g:1503:5: kw= '-'
                            {
                            kw=(Token)match(input,43,FOLLOW_28); 

                            					current.merge(kw);
                            					newLeafNode(kw, grammarAccess.getElementIDAccess().getHyphenMinusKeyword_3_0());
                            				

                            }
                            break;

                    }

                    this_INT_4=(Token)match(input,RULE_INT,FOLLOW_2); 

                    				current.merge(this_INT_4);
                    			

                    				newLeafNode(this_INT_4, grammarAccess.getElementIDAccess().getINTTerminalRuleCall_3_1());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleelementID"


    // $ANTLR start "entryRulePossiblySignedNumber"
    // InternalCTWedge.g:1521:1: entryRulePossiblySignedNumber returns [String current=null] : iv_rulePossiblySignedNumber= rulePossiblySignedNumber EOF ;
    public final String entryRulePossiblySignedNumber() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_rulePossiblySignedNumber = null;


        try {
            // InternalCTWedge.g:1521:60: (iv_rulePossiblySignedNumber= rulePossiblySignedNumber EOF )
            // InternalCTWedge.g:1522:2: iv_rulePossiblySignedNumber= rulePossiblySignedNumber EOF
            {
             newCompositeNode(grammarAccess.getPossiblySignedNumberRule()); 
            pushFollow(FOLLOW_1);
            iv_rulePossiblySignedNumber=rulePossiblySignedNumber();

            state._fsp--;

             current =iv_rulePossiblySignedNumber.getText(); 
            match(input,EOF,FOLLOW_2); 

            }

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePossiblySignedNumber"


    // $ANTLR start "rulePossiblySignedNumber"
    // InternalCTWedge.g:1528:1: rulePossiblySignedNumber returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : ( (kw= '-' )? this_INT_1= RULE_INT ) ;
    public final AntlrDatatypeRuleToken rulePossiblySignedNumber() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;
        Token this_INT_1=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1534:2: ( ( (kw= '-' )? this_INT_1= RULE_INT ) )
            // InternalCTWedge.g:1535:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            {
            // InternalCTWedge.g:1535:2: ( (kw= '-' )? this_INT_1= RULE_INT )
            // InternalCTWedge.g:1536:3: (kw= '-' )? this_INT_1= RULE_INT
            {
            // InternalCTWedge.g:1536:3: (kw= '-' )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==43) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // InternalCTWedge.g:1537:4: kw= '-'
                    {
                    kw=(Token)match(input,43,FOLLOW_28); 

                    				current.merge(kw);
                    				newLeafNode(kw, grammarAccess.getPossiblySignedNumberAccess().getHyphenMinusKeyword_0());
                    			

                    }
                    break;

            }

            this_INT_1=(Token)match(input,RULE_INT,FOLLOW_2); 

            			current.merge(this_INT_1);
            		

            			newLeafNode(this_INT_1, grammarAccess.getPossiblySignedNumberAccess().getINTTerminalRuleCall_1());
            		

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePossiblySignedNumber"


    // $ANTLR start "ruleRelationalOperators"
    // InternalCTWedge.g:1554:1: ruleRelationalOperators returns [Enumerator current=null] : ( (enumLiteral_0= '>' ) | (enumLiteral_1= '<' ) | (enumLiteral_2= '>=' ) | (enumLiteral_3= '<=' ) ) ;
    public final Enumerator ruleRelationalOperators() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1560:2: ( ( (enumLiteral_0= '>' ) | (enumLiteral_1= '<' ) | (enumLiteral_2= '>=' ) | (enumLiteral_3= '<=' ) ) )
            // InternalCTWedge.g:1561:2: ( (enumLiteral_0= '>' ) | (enumLiteral_1= '<' ) | (enumLiteral_2= '>=' ) | (enumLiteral_3= '<=' ) )
            {
            // InternalCTWedge.g:1561:2: ( (enumLiteral_0= '>' ) | (enumLiteral_1= '<' ) | (enumLiteral_2= '>=' ) | (enumLiteral_3= '<=' ) )
            int alt30=4;
            switch ( input.LA(1) ) {
            case 44:
                {
                alt30=1;
                }
                break;
            case 45:
                {
                alt30=2;
                }
                break;
            case 46:
                {
                alt30=3;
                }
                break;
            case 47:
                {
                alt30=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // InternalCTWedge.g:1562:3: (enumLiteral_0= '>' )
                    {
                    // InternalCTWedge.g:1562:3: (enumLiteral_0= '>' )
                    // InternalCTWedge.g:1563:4: enumLiteral_0= '>'
                    {
                    enumLiteral_0=(Token)match(input,44,FOLLOW_2); 

                    				current = grammarAccess.getRelationalOperatorsAccess().getGTEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getRelationalOperatorsAccess().getGTEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1570:3: (enumLiteral_1= '<' )
                    {
                    // InternalCTWedge.g:1570:3: (enumLiteral_1= '<' )
                    // InternalCTWedge.g:1571:4: enumLiteral_1= '<'
                    {
                    enumLiteral_1=(Token)match(input,45,FOLLOW_2); 

                    				current = grammarAccess.getRelationalOperatorsAccess().getLTEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getRelationalOperatorsAccess().getLTEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1578:3: (enumLiteral_2= '>=' )
                    {
                    // InternalCTWedge.g:1578:3: (enumLiteral_2= '>=' )
                    // InternalCTWedge.g:1579:4: enumLiteral_2= '>='
                    {
                    enumLiteral_2=(Token)match(input,46,FOLLOW_2); 

                    				current = grammarAccess.getRelationalOperatorsAccess().getGEEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getRelationalOperatorsAccess().getGEEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:1586:3: (enumLiteral_3= '<=' )
                    {
                    // InternalCTWedge.g:1586:3: (enumLiteral_3= '<=' )
                    // InternalCTWedge.g:1587:4: enumLiteral_3= '<='
                    {
                    enumLiteral_3=(Token)match(input,47,FOLLOW_2); 

                    				current = grammarAccess.getRelationalOperatorsAccess().getLEEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getRelationalOperatorsAccess().getLEEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelationalOperators"


    // $ANTLR start "ruleEqualityOperators"
    // InternalCTWedge.g:1597:1: ruleEqualityOperators returns [Enumerator current=null] : ( (enumLiteral_0= '==' ) | (enumLiteral_1= '!=' ) | (enumLiteral_2= '=' ) ) ;
    public final Enumerator ruleEqualityOperators() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1603:2: ( ( (enumLiteral_0= '==' ) | (enumLiteral_1= '!=' ) | (enumLiteral_2= '=' ) ) )
            // InternalCTWedge.g:1604:2: ( (enumLiteral_0= '==' ) | (enumLiteral_1= '!=' ) | (enumLiteral_2= '=' ) )
            {
            // InternalCTWedge.g:1604:2: ( (enumLiteral_0= '==' ) | (enumLiteral_1= '!=' ) | (enumLiteral_2= '=' ) )
            int alt31=3;
            switch ( input.LA(1) ) {
            case 48:
                {
                alt31=1;
                }
                break;
            case 49:
                {
                alt31=2;
                }
                break;
            case 50:
                {
                alt31=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // InternalCTWedge.g:1605:3: (enumLiteral_0= '==' )
                    {
                    // InternalCTWedge.g:1605:3: (enumLiteral_0= '==' )
                    // InternalCTWedge.g:1606:4: enumLiteral_0= '=='
                    {
                    enumLiteral_0=(Token)match(input,48,FOLLOW_2); 

                    				current = grammarAccess.getEqualityOperatorsAccess().getEQEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getEqualityOperatorsAccess().getEQEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1613:3: (enumLiteral_1= '!=' )
                    {
                    // InternalCTWedge.g:1613:3: (enumLiteral_1= '!=' )
                    // InternalCTWedge.g:1614:4: enumLiteral_1= '!='
                    {
                    enumLiteral_1=(Token)match(input,49,FOLLOW_2); 

                    				current = grammarAccess.getEqualityOperatorsAccess().getNEEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getEqualityOperatorsAccess().getNEEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1621:3: (enumLiteral_2= '=' )
                    {
                    // InternalCTWedge.g:1621:3: (enumLiteral_2= '=' )
                    // InternalCTWedge.g:1622:4: enumLiteral_2= '='
                    {
                    enumLiteral_2=(Token)match(input,50,FOLLOW_2); 

                    				current = grammarAccess.getEqualityOperatorsAccess().getEQEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getEqualityOperatorsAccess().getEQEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEqualityOperators"


    // $ANTLR start "rulePlusMinusOperators"
    // InternalCTWedge.g:1632:1: rulePlusMinusOperators returns [Enumerator current=null] : ( (enumLiteral_0= '+' ) | (enumLiteral_1= '-' ) ) ;
    public final Enumerator rulePlusMinusOperators() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1638:2: ( ( (enumLiteral_0= '+' ) | (enumLiteral_1= '-' ) ) )
            // InternalCTWedge.g:1639:2: ( (enumLiteral_0= '+' ) | (enumLiteral_1= '-' ) )
            {
            // InternalCTWedge.g:1639:2: ( (enumLiteral_0= '+' ) | (enumLiteral_1= '-' ) )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==51) ) {
                alt32=1;
            }
            else if ( (LA32_0==43) ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // InternalCTWedge.g:1640:3: (enumLiteral_0= '+' )
                    {
                    // InternalCTWedge.g:1640:3: (enumLiteral_0= '+' )
                    // InternalCTWedge.g:1641:4: enumLiteral_0= '+'
                    {
                    enumLiteral_0=(Token)match(input,51,FOLLOW_2); 

                    				current = grammarAccess.getPlusMinusOperatorsAccess().getPlusEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getPlusMinusOperatorsAccess().getPlusEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1648:3: (enumLiteral_1= '-' )
                    {
                    // InternalCTWedge.g:1648:3: (enumLiteral_1= '-' )
                    // InternalCTWedge.g:1649:4: enumLiteral_1= '-'
                    {
                    enumLiteral_1=(Token)match(input,43,FOLLOW_2); 

                    				current = grammarAccess.getPlusMinusOperatorsAccess().getMinusEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getPlusMinusOperatorsAccess().getMinusEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePlusMinusOperators"


    // $ANTLR start "ruleModMultDivOperators"
    // InternalCTWedge.g:1659:1: ruleModMultDivOperators returns [Enumerator current=null] : ( (enumLiteral_0= '%' ) | (enumLiteral_1= '*' ) | (enumLiteral_2= '/' ) ) ;
    public final Enumerator ruleModMultDivOperators() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1665:2: ( ( (enumLiteral_0= '%' ) | (enumLiteral_1= '*' ) | (enumLiteral_2= '/' ) ) )
            // InternalCTWedge.g:1666:2: ( (enumLiteral_0= '%' ) | (enumLiteral_1= '*' ) | (enumLiteral_2= '/' ) )
            {
            // InternalCTWedge.g:1666:2: ( (enumLiteral_0= '%' ) | (enumLiteral_1= '*' ) | (enumLiteral_2= '/' ) )
            int alt33=3;
            switch ( input.LA(1) ) {
            case 52:
                {
                alt33=1;
                }
                break;
            case 53:
                {
                alt33=2;
                }
                break;
            case 54:
                {
                alt33=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // InternalCTWedge.g:1667:3: (enumLiteral_0= '%' )
                    {
                    // InternalCTWedge.g:1667:3: (enumLiteral_0= '%' )
                    // InternalCTWedge.g:1668:4: enumLiteral_0= '%'
                    {
                    enumLiteral_0=(Token)match(input,52,FOLLOW_2); 

                    				current = grammarAccess.getModMultDivOperatorsAccess().getModEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getModMultDivOperatorsAccess().getModEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1675:3: (enumLiteral_1= '*' )
                    {
                    // InternalCTWedge.g:1675:3: (enumLiteral_1= '*' )
                    // InternalCTWedge.g:1676:4: enumLiteral_1= '*'
                    {
                    enumLiteral_1=(Token)match(input,53,FOLLOW_2); 

                    				current = grammarAccess.getModMultDivOperatorsAccess().getMultEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getModMultDivOperatorsAccess().getMultEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1683:3: (enumLiteral_2= '/' )
                    {
                    // InternalCTWedge.g:1683:3: (enumLiteral_2= '/' )
                    // InternalCTWedge.g:1684:4: enumLiteral_2= '/'
                    {
                    enumLiteral_2=(Token)match(input,54,FOLLOW_2); 

                    				current = grammarAccess.getModMultDivOperatorsAccess().getDivEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getModMultDivOperatorsAccess().getDivEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModMultDivOperators"


    // $ANTLR start "ruleImpliesOperator"
    // InternalCTWedge.g:1694:1: ruleImpliesOperator returns [Enumerator current=null] : ( (enumLiteral_0= '=>' ) | (enumLiteral_1= '<=>' ) | (enumLiteral_2= '->' ) | (enumLiteral_3= '<->' ) ) ;
    public final Enumerator ruleImpliesOperator() throws RecognitionException {
        Enumerator current = null;

        Token enumLiteral_0=null;
        Token enumLiteral_1=null;
        Token enumLiteral_2=null;
        Token enumLiteral_3=null;


        	enterRule();

        try {
            // InternalCTWedge.g:1700:2: ( ( (enumLiteral_0= '=>' ) | (enumLiteral_1= '<=>' ) | (enumLiteral_2= '->' ) | (enumLiteral_3= '<->' ) ) )
            // InternalCTWedge.g:1701:2: ( (enumLiteral_0= '=>' ) | (enumLiteral_1= '<=>' ) | (enumLiteral_2= '->' ) | (enumLiteral_3= '<->' ) )
            {
            // InternalCTWedge.g:1701:2: ( (enumLiteral_0= '=>' ) | (enumLiteral_1= '<=>' ) | (enumLiteral_2= '->' ) | (enumLiteral_3= '<->' ) )
            int alt34=4;
            switch ( input.LA(1) ) {
            case 55:
                {
                alt34=1;
                }
                break;
            case 56:
                {
                alt34=2;
                }
                break;
            case 57:
                {
                alt34=3;
                }
                break;
            case 58:
                {
                alt34=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // InternalCTWedge.g:1702:3: (enumLiteral_0= '=>' )
                    {
                    // InternalCTWedge.g:1702:3: (enumLiteral_0= '=>' )
                    // InternalCTWedge.g:1703:4: enumLiteral_0= '=>'
                    {
                    enumLiteral_0=(Token)match(input,55,FOLLOW_2); 

                    				current = grammarAccess.getImpliesOperatorAccess().getImplEnumLiteralDeclaration_0().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_0, grammarAccess.getImpliesOperatorAccess().getImplEnumLiteralDeclaration_0());
                    			

                    }


                    }
                    break;
                case 2 :
                    // InternalCTWedge.g:1710:3: (enumLiteral_1= '<=>' )
                    {
                    // InternalCTWedge.g:1710:3: (enumLiteral_1= '<=>' )
                    // InternalCTWedge.g:1711:4: enumLiteral_1= '<=>'
                    {
                    enumLiteral_1=(Token)match(input,56,FOLLOW_2); 

                    				current = grammarAccess.getImpliesOperatorAccess().getIffEnumLiteralDeclaration_1().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_1, grammarAccess.getImpliesOperatorAccess().getIffEnumLiteralDeclaration_1());
                    			

                    }


                    }
                    break;
                case 3 :
                    // InternalCTWedge.g:1718:3: (enumLiteral_2= '->' )
                    {
                    // InternalCTWedge.g:1718:3: (enumLiteral_2= '->' )
                    // InternalCTWedge.g:1719:4: enumLiteral_2= '->'
                    {
                    enumLiteral_2=(Token)match(input,57,FOLLOW_2); 

                    				current = grammarAccess.getImpliesOperatorAccess().getImplEnumLiteralDeclaration_2().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_2, grammarAccess.getImpliesOperatorAccess().getImplEnumLiteralDeclaration_2());
                    			

                    }


                    }
                    break;
                case 4 :
                    // InternalCTWedge.g:1726:3: (enumLiteral_3= '<->' )
                    {
                    // InternalCTWedge.g:1726:3: (enumLiteral_3= '<->' )
                    // InternalCTWedge.g:1727:4: enumLiteral_3= '<->'
                    {
                    enumLiteral_3=(Token)match(input,58,FOLLOW_2); 

                    				current = grammarAccess.getImpliesOperatorAccess().getIffEnumLiteralDeclaration_3().getEnumLiteral().getInstance();
                    				newLeafNode(enumLiteral_3, grammarAccess.getImpliesOperatorAccess().getIffEnumLiteralDeclaration_3());
                    			

                    }


                    }
                    break;

            }


            }


            	leaveRule();

        }

            catch (RecognitionException re) {
                recover(input,re);
                appendSkippedTokens();
            }
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleImpliesOperator"

    // Delegated rules


 

    public static final BitSet FOLLOW_1 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_2 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_3 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_4 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_5 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_6 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_7 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_8 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_9 = new BitSet(new long[]{0x0000000000010002L});
    public static final BitSet FOLLOW_10 = new BitSet(new long[]{0x0000000000060000L});
    public static final BitSet FOLLOW_11 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_12 = new BitSet(new long[]{0x0000000000300000L});
    public static final BitSet FOLLOW_13 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_14 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_15 = new BitSet(new long[]{0x0000000000180000L});
    public static final BitSet FOLLOW_16 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_17 = new BitSet(new long[]{0x0000000001100000L});
    public static final BitSet FOLLOW_18 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_19 = new BitSet(new long[]{0x0000000000900000L});
    public static final BitSet FOLLOW_20 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_21 = new BitSet(new long[]{0x00000800000000F0L});
    public static final BitSet FOLLOW_22 = new BitSet(new long[]{0x00000800005000F0L});
    public static final BitSet FOLLOW_23 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_24 = new BitSet(new long[]{0x0000080000000020L});
    public static final BitSet FOLLOW_25 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_29 = new BitSet(new long[]{0x00000F0041A800F0L});
    public static final BitSet FOLLOW_30 = new BitSet(new long[]{0x0780000000000002L});
    public static final BitSet FOLLOW_31 = new BitSet(new long[]{0x0000000F00000002L});
    public static final BitSet FOLLOW_32 = new BitSet(new long[]{0x000000F000000002L});
    public static final BitSet FOLLOW_33 = new BitSet(new long[]{0x0007000000000002L});
    public static final BitSet FOLLOW_34 = new BitSet(new long[]{0x0000F00000000002L});
    public static final BitSet FOLLOW_35 = new BitSet(new long[]{0x0008080000000002L});
    public static final BitSet FOLLOW_36 = new BitSet(new long[]{0x0070000000000002L});
    public static final BitSet FOLLOW_37 = new BitSet(new long[]{0x0000000080000000L});

}